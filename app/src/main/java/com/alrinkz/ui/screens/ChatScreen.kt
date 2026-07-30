package com.alrinkz.ui.screens

import com.alrinkz.ui.viewmodels.*
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alrinkz.data.local.ChatMessageEntity
import com.alrinkz.ui.components.ActivePulse
import com.alrinkz.ui.components.GlassmorphicCard
import com.alrinkz.ui.components.StatusBadge
import com.alrinkz.ui.components.TerminalLogLine
import org.json.JSONArray

data class PlannerStep(val title: String, val status: String)

fun parsePlannerSteps(json: String?): List<PlannerStep> {
    if (json == null) return emptyList()
    return try {
        val list = mutableListOf<PlannerStep>()
        val arr = JSONArray(json)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                PlannerStep(
                    title = obj.optString("title", ""),
                    status = obj.optString("status", "pending")
                )
            )
        }
        list
    } catch (e: Exception) {
        emptyList()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    val quickPrompts = listOf(
        "Compile Startup Runway Dashboard",
        "Audit Repository Security Metrics",
        "Synchronize Team Standup updates"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()
    ) {
        // Redesigned Premium Top Bar with pulse indicators
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "Core",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "ALRIN OS CORE",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            ActivePulse(color = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "COO NODE ACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            actions = {
                IconButton(onClick = { viewModel.triggerForceSync() }) {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = "Sync Data",
                        tint = if (isSyncing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onBackground
            )
        )

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Welcome Card when no messages are present
            if (messages.isEmpty()) {
                item {
                    WelcomeCard()
                }
            }

            items(messages) { message ->
                PremiumChatBubble(message)
            }

            if (isTyping) {
                item {
                    Row(
                        modifier = Modifier.padding(start = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActivePulse(color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Orchestrating agent workflows...",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Suggestions block
        if (messages.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "INITIALIZE AUTONOMOUS CO-OPERATIVE ACTION:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                quickPrompts.forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.sendMessage(prompt) }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Run",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        // Interactive Input Panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = {
                    Text(
                        "Command Alrin OS...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            IconButton(
                onClick = {
                    if (textState.text.isNotBlank()) {
                        viewModel.sendMessage(textState.text)
                        textState = TextFieldValue("")
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(24.dp))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Command",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun WelcomeCard() {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "SYSTEM INITIALIZED",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.5.sp,
                fontSize = 12.sp
            )
            Text(
                text = "Welcome to ALRIN (Autonomous COO Operating System)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "ALRIN operates in the background to monitor connected developer workspaces, run security audits, schedule financial forecasts, and alert teams. Direct the core node using standard conversational text or precise shell-like actions.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun PremiumChatBubble(message: ChatMessageEntity) {
    val isUser = message.isUser

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        if (isUser) {
            // User Chat Bubble: Elegant, minimalist right-aligned pill
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 4.dp
                ),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Text(
                    text = message.text,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            // AI Autonomous COO Log/Response Block
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(0.95f),
                borderColor = if (message.structuredType == "planner") MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Message Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "COO Node",
                                tint = if (message.structuredType == "planner") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "ALRIN CORE PROCESSOR",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        StatusBadge(
                            text = message.structuredType,
                            color = if (message.structuredType == "planner") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Main Text Content
                    Text(
                        text = message.text,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )

                    // Planner Steps (if planner json is present)
                    val steps = parsePlannerSteps(message.plannerStepsJson)
                    if (steps.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF070913), shape = RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "AUTONOMOUS PLANNER FLOW",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                            steps.forEach { step ->
                                val icon = when (step.status) {
                                    "completed" -> Icons.Default.Done
                                    "running" -> Icons.Default.PlayArrow
                                    else -> Icons.Default.Pending
                                }
                                val iconColor = when (step.status) {
                                    "completed" -> MaterialTheme.colorScheme.primary
                                    "running" -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = step.status,
                                        tint = iconColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = step.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (step.status == "completed") MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    // Tool logs / terminal outputs
                    message.toolLogs?.let { logs ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "WORKSPACE API EXECUTION LOGS:",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                            logs.split("\n").forEach { logLine ->
                                if (logLine.isNotBlank()) {
                                    TerminalLogLine(text = logLine)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}