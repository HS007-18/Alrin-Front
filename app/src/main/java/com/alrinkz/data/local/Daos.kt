package com.alrinkz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("DELETE FROM chat_messages")
    suspend fun clearMessages()
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memory_nodes ORDER BY timestamp DESC")
    fun getAllNodes(): Flow<List<MemoryNodeEntity>>

    @Query("SELECT * FROM memory_nodes WHERE category = :category ORDER BY timestamp DESC")
    fun getNodesByCategory(category: String): Flow<List<MemoryNodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNode(node: MemoryNodeEntity)

    @Query("DELETE FROM memory_nodes WHERE id = :id")
    suspend fun deleteNodeById(id: String)

    @Query("DELETE FROM memory_nodes")
    suspend fun clearNodes()
}

@Dao
interface JobDao {
    @Query("SELECT * FROM agent_jobs ORDER BY startedAt DESC")
    fun getAllJobs(): Flow<List<AgentJobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: AgentJobEntity)

    @Query("DELETE FROM agent_jobs")
    suspend fun clearJobs()
}

@Dao
interface IntegrationDao {
    @Query("SELECT * FROM integrations")
    fun getAllIntegrations(): Flow<List<IntegrationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntegrations(integrations: List<IntegrationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntegration(integration: IntegrationEntity)
}