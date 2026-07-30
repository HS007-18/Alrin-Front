package com.aistudio.alrinkz.xzyy

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.alrinkz.xzyy.data.local.AgentJobEntity
import com.aistudio.alrinkz.xzyy.data.local.ChatMessageEntity
import com.aistudio.alrinkz.xzyy.data.local.IntegrationEntity
import com.aistudio.alrinkz.xzyy.data.local.MemoryNodeEntity
import com.aistudio.alrinkz.xzyy.data.repository.AlrinRepository
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
            repository.sendMessage(text)
            _isTyping.value = false
        }
    }

    fun addMemory(content: String, category: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _isSyncing.value = true
            repository.addMemory(content, category)
            _isSyncing.value = false
        }
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.deleteMemory(id)
            _isSyncing.value = false
        }
    }

    fun connectIntegration(id: String, onUrlReceived: (String) -> Unit) {
        viewModelScope.launch {
            _isSyncing.value = true
            val url = repository.getIntegrationAuthUrl(id)
            if (url != null) {
                onUrlReceived(url)
            }
            _isSyncing.value = false
        }
    }

    fun disconnectIntegration(id: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.disconnectIntegration(id)
            _isSyncing.value = false
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
            repository.syncMessages()
            repository.syncMemory()
            repository.syncIntegrations()
            repository.syncJobs()
            _isSyncing.value = false
        }
    }
}
