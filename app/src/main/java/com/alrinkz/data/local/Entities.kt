package com.alrinkz.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long,
    val senderName: String,
    // Store JSON structured content if any (e.g. planner steps, tool logs)
    val structuredType: String = "text", // "text", "planner", "tool_execution"
    val toolLogs: String? = null,
    val plannerStepsJson: String? = null
)

@Entity(tableName = "memory_nodes")
data class MemoryNodeEntity(
    @PrimaryKey val id: String,
    val content: String,
    val category: String, // "fact", "rule", "document", "key"
    val timestamp: Long,
    val vectorId: String? = null,
    val source: String = "User"
)

@Entity(tableName = "agent_jobs")
data class AgentJobEntity(
    @PrimaryKey val id: String,
    val title: String,
    val status: String, // "pending", "running", "completed", "failed"
    val progress: Float,
    val startedAt: Long,
    val completedAt: Long? = null,
    val plannerStepsJson: String? = null,
    val currentStepIndex: Int = 0
)

@Entity(tableName = "integrations")
data class IntegrationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val isConnected: Boolean,
    val lastSyncTime: Long? = null,
    val scopesJson: String? = null
)