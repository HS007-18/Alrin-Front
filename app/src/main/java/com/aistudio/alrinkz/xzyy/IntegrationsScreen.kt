package com.aistudio.alrinkz.xzyy

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aistudio.alrinkz.xzyy.data.local.IntegrationEntity
import com.aistudio.alrinkz.xzyy.ui.components.ActivePulse
import com.aistudio.alrinkz.xzyy.ui.components.GlassmorphicCard
import com.aistudio.alrinkz.xzyy.ui.components.StatusBadge
import com.aistudio.alrinkz.xzyy.ui.components.TerminalLogLine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntegrationsScreen(viewModel: ChatViewModel = viewModel()) {
    val integrations by viewModel.integrations.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Redesigned Top Bar for Integrations
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Extension,
                        contentDescription = "Integrations Engine",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "COMPOSIO ECOSYSTEMS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "CONNECTED AUTONOMOUS AGENT CHANNELS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            actions = {
                IconButton(onClick = { viewModel.triggerForceSync() }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Sync Integrations",
                        tint = if (isSyncing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        // Ecosystem Health Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFF0F111A), shape = RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ECOSYSTEM STATUS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    val activeCount = integrations.count { it.isConnected }
                    Text(
                        text = "$activeCount / ${integrations.size} ACTIVE CHANNELS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = if (activeCount > 0) MaterialTheme.colorScheme.primary else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Composio manages token handshakes and scopes for autonomous background tasks securely. Connect to grant core agent execution privileges.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    lineHeight = 14.sp
                )
            }
        }

        // Integration Card List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(integrations) { integration ->
                IntegrationCard(
                    integration = integration,
                    onConnect = {
                        viewModel.connectIntegration(integration.id) { authUrl ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
                            context.startActivity(intent)
                        }
                    },
                    onDisconnect = {
                        viewModel.disconnectIntegration(integration.id)
                    }
                )
            }
        }
    }
}

@Composable
fun IntegrationCard(
    integration: IntegrationEntity,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    val activeColor = if (integration.isConnected) MaterialTheme.colorScheme.primary else Color.Gray
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (integration.isConnected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Render modern visual icons based on ID
                    val icon = when (integration.id) {
                        "github" -> Icons.Default.Source
                        "slack" -> Icons.Default.Forum
                        "gdrive" -> Icons.Default.FolderOpen
                        else -> Icons.Default.Book
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(activeColor.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = integration.name,
                            tint = activeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = integration.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (integration.isConnected) {
                                ActivePulse(color = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = "AUTHORIZED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color.Gray, shape = RoundedCornerShape(50))
                                )
                                Text(
                                    text = "DISCONNECTED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                if (integration.isConnected) {
                    TextButton(
                        onClick = onDisconnect,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Disconnect", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onConnect,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Connect", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                text = integration.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                lineHeight = 18.sp
            )

            if (integration.isConnected && integration.lastSyncTime != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Last sync automatic check:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Text(
                        text = dateFormat.format(Date(integration.lastSyncTime)),
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
