package com.alrinkz.ui.screens

import com.alrinkz.ui.viewmodels.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alrinkz.ui.components.ActivePulse
import com.alrinkz.ui.components.GlassmorphicCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: ChatViewModel = viewModel()) {
    val currentUrl by viewModel.apiUrl.collectAsState()
    val currentToken by viewModel.authToken.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    var urlInput by remember { mutableStateOf(currentUrl) }
    var tokenInput by remember { mutableStateOf(currentToken ?: "") }
    var isTokenVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Redesigned Top Bar for Settings
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings Engine",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "SYSTEM PREFERENCES",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "ALRIN CORE ENGINE ROUTER & PROTOCOLS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            // Server Endpoint config
            item {
                Text(
                    text = "CORE REPOSITORY ENDPOINT",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "ALRIN's central tasks, vector database coordinates, and secure model execution keys are located on your remote FastAPI instances.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            label = { Text("Core Endpoint URL") },
                            leadingIcon = { Icon(Icons.Default.Dns, contentDescription = "Endpoint") },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )

                        OutlinedTextField(
                            value = tokenInput,
                            onValueChange = { tokenInput = it },
                            label = { Text("System Handshake Auth Token") },
                            leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = "Token") },
                            trailingIcon = {
                                IconButton(onClick = { isTokenVisible = !isTokenVisible }) {
                                    Icon(
                                        if (isTokenVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle token visibility"
                                    )
                                }
                            },
                            visualTransformation = if (isTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )

                        Button(
                            onClick = {
                                viewModel.updateConfig(urlInput, tokenInput.ifBlank { null })
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save Configuration")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SAVE & COUPLING Handshake", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // AI Routing Engine status
            item {
                Text(
                    text = "SERVER-SIDE AI ROUTING PROFILE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ROUTING PROFILE:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                ActivePulse(color = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = "SECURE CLOUD ROUTE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = "Active Server Route: Gemini 1.5 Pro (Operational Task Analysis) & Claude 3.5 Sonnet (Security Audits & Reports). Routing decisions are evaluated server-side based on system priority factors.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = "Shield",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Client safety: AI API keys are never stored on this device.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // Developer Utilities and Tools
            item {
                Text(
                    text = "LOCAL UTILITIES & DIAGNOSTICS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.triggerForceSync() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = "Full Sync", tint = MaterialTheme.colorScheme.onBackground)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("FORCE COGNITIVE RE-SYNC", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground)
                        }

                        Text(
                            text = "Triggers complete vector indexing alignment with FastAPI cache repositories, validating authorization channels.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}