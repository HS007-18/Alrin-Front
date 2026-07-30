package com.alrinkz.data.remote

import retrofit2.http.*

data class LoginRequest(val email: String, val secret: String)
data class LoginResponse(val accessToken: String, val tokenType: String)

data class MessageDto(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long,
    val senderName: String,
    val structuredType: String,
    val toolLogs: String?,
    val plannerStepsJson: String?
)

data class SendMessageRequest(val text: String, val streamResponse: Boolean = false)

data class MemoryNodeDto(
    val id: String,
    val content: String,
    val category: String,
    val timestamp: Long,
    val vectorId: String?,
    val source: String
)

data class AddMemoryRequest(val content: String, val category: String)

data class JobDto(
    val id: String,
    val title: String,
    val status: String,
    val progress: Float,
    val startedAt: Long,
    val completedAt: Long?,
    val plannerStepsJson: String?,
    val currentStepIndex: Int
)

data class IntegrationDto(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val isConnected: Boolean,
    val lastSyncTime: Long?,
    val scopesJson: String?
)

data class ToggleIntegrationRequest(val id: String, val enable: Boolean)

data class ConnectResponse(val authUrl: String)

interface AlrinApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/v1/chat/messages")
    suspend fun getMessages(@Header("Authorization") token: String): List<MessageDto>

    @POST("api/v1/chat/send")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Body request: SendMessageRequest
    ): MessageDto

    @GET("api/v1/memory/nodes")
    suspend fun getMemoryNodes(@Header("Authorization") token: String): List<MemoryNodeDto>

    @POST("api/v1/memory/add")
    suspend fun addMemoryNode(
        @Header("Authorization") token: String,
        @Body request: AddMemoryRequest
    ): MemoryNodeDto

    @DELETE("api/v1/memory/delete/{id}")
    suspend fun deleteMemoryNode(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Void

    @GET("api/v1/integrations")
    suspend fun getIntegrations(@Header("Authorization") token: String): List<IntegrationDto>

    @GET("api/v1/integrations/{id}/connect")
    suspend fun getIntegrationAuthUrl(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ConnectResponse

    @POST("api/v1/integrations/{id}/disconnect")
    suspend fun disconnectIntegration(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): IntegrationDto

    @GET("api/v1/jobs")
    suspend fun getAgentJobs(@Header("Authorization") token: String): List<JobDto>
}