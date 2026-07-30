package com.alrinkz.data.repository

import android.util.Log
import com.alrinkz.data.local.*
import com.alrinkz.data.remote.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.UUID

class AlrinRepository(private val db: AppDatabase) {

    private val chatDao = db.chatDao()
    private val memoryDao = db.memoryDao()
    private val jobDao = db.jobDao()
    private val integrationDao = db.integrationDao()

    // Default base URL for the hosted FastAPI. Users can override this in settings.
    private var baseUrl = "https://alrin-backend.onrender.com/"
    private var api: AlrinApi = createApi(baseUrl)
    private var authToken: String? = null

    companion object {
        private const val TAG = "AlrinRepository"
    }

    private fun createApi(url: String): AlrinApi {
        val sanitizedUrl = if (url.endsWith("/")) url else "$url/"
        return try {
            Retrofit.Builder()
                .baseUrl(sanitizedUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AlrinApi::class.java)
        } catch (e: Exception) {
            // Fallback for malformed URLs
            Retrofit.Builder()
                .baseUrl("https://alrin-backend.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AlrinApi::class.java)
        }
    }

    fun updateBaseUrl(newUrl: String) {
        baseUrl = newUrl
        api = createApi(newUrl)
    }

    fun setToken(token: String?) {
        authToken = if (token != null && !token.startsWith("Bearer ")) "Bearer $token" else token
    }

    fun getMessagesFlow(): Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()
    fun getMemoryFlow(): Flow<List<MemoryNodeEntity>> = memoryDao.getAllNodes()
    fun getJobsFlow(): Flow<List<AgentJobEntity>> = jobDao.getAllJobs()
    fun getIntegrationsFlow(): Flow<List<IntegrationEntity>> = integrationDao.getAllIntegrations()

    // Synchronize messages with the backend
    suspend fun syncMessages() {
        val token = authToken ?: return
        try {
            val remoteMessages = api.getMessages(token)
            val entities = remoteMessages.map {
                ChatMessageEntity(
                    id = it.id,
                    text = it.text,
                    isUser = it.isUser,
                    timestamp = it.timestamp,
                    senderName = it.senderName,
                    structuredType = it.structuredType,
                    toolLogs = it.toolLogs,
                    plannerStepsJson = it.plannerStepsJson
                )
            }
            chatDao.clearMessages()
            chatDao.insertMessages(entities)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync messages: ${e.message}")
            // Fail silently or rely on cache offline
        }
    }

    // Send a message with optimistic local insert
    suspend fun sendMessage(text: String): Boolean {
        val messageId = UUID.randomUUID().toString()
        val localMsg = ChatMessageEntity(
            id = messageId,
            text = text,
            isUser = true,
            timestamp = System.currentTimeMillis(),
            senderName = "User"
        )
        // Optimistic UI updates
        chatDao.insertMessage(localMsg)

        val token = authToken
        if (token == null) {
            Log.e(TAG, "Cannot send message, no auth token")
            return false
        }

        return try {
            val response = api.sendMessage(token, SendMessageRequest(text))
            // Replace optimistic message with actual network message or update it
            chatDao.insertMessage(
                ChatMessageEntity(
                    id = response.id,
                    text = response.text,
                    isUser = response.isUser,
                    timestamp = response.timestamp,
                    senderName = response.senderName,
                    structuredType = response.structuredType,
                    toolLogs = response.toolLogs,
                    plannerStepsJson = response.plannerStepsJson
                )
            )
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message over network: ${e.message}")
            false
        }
    }

    // Sync Memory Nodes
    suspend fun syncMemory() {
        val token = authToken ?: return
        try {
            val remoteNodes = api.getMemoryNodes(token)
            val entities = remoteNodes.map {
                MemoryNodeEntity(
                    id = it.id,
                    content = it.content,
                    category = it.category,
                    timestamp = it.timestamp,
                    vectorId = it.vectorId,
                    source = it.source
                )
            }
            memoryDao.clearNodes()
            for (node in entities) {
                memoryDao.insertNode(node)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync memory: ${e.message}")
        }
    }

    // Add Memory Node
    suspend fun addMemory(content: String, category: String): Boolean {
        val localId = UUID.randomUUID().toString()
        val localNode = MemoryNodeEntity(
            id = localId,
            content = content,
            category = category,
            timestamp = System.currentTimeMillis()
        )
        memoryDao.insertNode(localNode)

        val token = authToken ?: return true // Local success is good enough offline
        return try {
            val remote = api.addMemoryNode(token, AddMemoryRequest(content, category))
            // Overwrite with network result
            memoryDao.deleteNodeById(localId)
            memoryDao.insertNode(
                MemoryNodeEntity(
                    id = remote.id,
                    content = remote.content,
                    category = remote.category,
                    timestamp = remote.timestamp,
                    vectorId = remote.vectorId,
                    source = remote.source
                )
            )
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save remote memory node: ${e.message}")
            true
        }
    }

    // Delete Memory Node
    suspend fun deleteMemory(id: String): Boolean {
        memoryDao.deleteNodeById(id)
        val token = authToken ?: return true
        return try {
            api.deleteMemoryNode(token, id)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete remote memory node: ${e.message}")
            false
        }
    }

    // Sync Integrations
    suspend fun syncIntegrations() {
        val token = authToken
        if (token == null) {
            Log.e(TAG, "Cannot sync integrations, no auth token")
            return
        }

        try {
            val remote = api.getIntegrations(token)
            val entities = remote.map {
                IntegrationEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    iconName = it.iconName,
                    isConnected = it.isConnected,
                    lastSyncTime = it.lastSyncTime,
                    scopesJson = it.scopesJson
                )
            }
            integrationDao.insertIntegrations(entities)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync integrations: ${e.message}")
        }
    }

    // Connect Integration Flow
    suspend fun getIntegrationAuthUrl(id: String): String? {
        val token = authToken ?: return null
        return try {
            val response = api.getIntegrationAuthUrl(token, id)
            response.authUrl
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get auth url for integration: ${e.message}")
            null
        }
    }

    // Disconnect Integration
    suspend fun disconnectIntegration(id: String): Boolean {
        val token = authToken ?: return false
        return try {
            val remote = api.disconnectIntegration(token, id)
            integrationDao.insertIntegration(
                IntegrationEntity(
                    id = remote.id,
                    name = remote.name,
                    description = remote.description,
                    iconName = remote.iconName,
                    isConnected = remote.isConnected,
                    lastSyncTime = remote.lastSyncTime,
                    scopesJson = remote.scopesJson
                )
            )
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to disconnect integration on backend: ${e.message}")
            false
        }
    }

    // Sync Agent Jobs
    suspend fun syncJobs() {
        val token = authToken
        if (token == null) {
            Log.e(TAG, "Cannot sync jobs, no auth token")
            return
        }

        try {
            val remote = api.getAgentJobs(token)
            val entities = remote.map {
                AgentJobEntity(
                    id = it.id,
                    title = it.title,
                    status = it.status,
                    progress = it.progress,
                    startedAt = it.startedAt,
                    completedAt = it.completedAt,
                    plannerStepsJson = it.plannerStepsJson,
                    currentStepIndex = it.currentStepIndex
                )
            }
            jobDao.clearJobs()
            for (job in entities) {
                jobDao.insertJob(job)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync agent jobs: ${e.message}")
        }
    }
}