package com.alrinkz.ui.viewmodels

import com.alrinkz.AlrinApp
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alrinkz.data.local.AgentJobEntity
import com.alrinkz.data.local.ChatMessageEntity
import com.alrinkz.data.local.IntegrationEntity
import com.alrinkz.data.local.MemoryNodeEntity
import com.alrinkz.data.repository.AlrinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AlrinRepository = (application as AlrinApp).repository

    // Reactive streams from database Cache
    val messages: StateFlow<List<ChatMessageEntity>> = repository.getMessagesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memoryNodes: StateFlow<List<MemoryNodeEntity>> = repository.getMemoryFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val agentJobs: StateFlow<List<AgentJobEntity>> = repository.getJobsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val integrations: StateFlow<List<IntegrationEntity>> = repository.getIntegrationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Loading & State Flags
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _apiUrl = MutableStateFlow("https://alrin-backend.onrender.com/")
    val apiUrl: StateFlow<String> = _apiUrl.asStateFlow()

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearError() {
        _errorMessage.value = null
    }

    init {
        // Initial setup and trigger seed/sync data
        viewModelScope.launch {
            _isSyncing.value = true
            repository.syncIntegrations()
            repository.syncJobs()
            repository.syncMessages()
            repository.syncMemory()
            _isSyncing.value = false
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isTyping.value = true
            try {
                repository.sendMessage(text)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to send message: ${e.message}"
            } finally {
                _isTyping.value = false
            }
        }
    }

    fun addMemory(content: String, category: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                repository.addMemory(content, category)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add memory: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                repository.deleteMemory(id)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete memory: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun connectIntegration(id: String, onUrlReceived: (String) -> Unit) {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                val url = repository.getIntegrationAuthUrl(id)
                if (url != null) {
                    onUrlReceived(url)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to connect: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun disconnectIntegration(id: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                repository.disconnectIntegration(id)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to disconnect: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun updateConfig(url: String, token: String?) {
        _apiUrl.value = url
        _authToken.value = token
        repository.updateBaseUrl(url)
        repository.setToken(token)
        
        // Trigger refetch with new credentials
        viewModelScope.launch {
            _isSyncing.value = true
            repository.syncMessages()
            repository.syncMemory()
            repository.syncIntegrations()
            repository.syncJobs()
            _isSyncing.value = false
        }
    }

    fun triggerForceSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                repository.syncMessages()
                repository.syncMemory()
                repository.syncIntegrations()
                repository.syncJobs()
            } catch (e: Exception) {
                _errorMessage.value = "Sync failed: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }
}