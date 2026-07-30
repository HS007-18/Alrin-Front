package com.aistudio.alrinkz.xzyy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aistudio.alrinkz.xzyy.data.local.MemoryNodeEntity
import com.aistudio.alrinkz.xzyy.ui.components.GlassmorphicCard
import com.aistudio.alrinkz.xzyy.ui.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(viewModel: ChatViewModel = viewModel()) {
    val nodes by viewModel.memoryNodes.collectAsState()
    var searchState by remember { mutableStateOf("") }
    var inputContent by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("fact") } // "fact", "rule", "document"
    var showAddPanel by remember { mutableStateOf(false) }

    val categories = listOf("fact", "rule", "document")

    // Filter nodes based on search state
    val filteredNodes = nodes.filter {
        it.content.contains(searchState, ignoreCase = true) ||
        it.category.contains(searchState, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Redesigned Top Bar for Memory
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Memory,
                        contentDescription = "Memory Engine",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "SEMANTIC MEMORY ENGINE",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "LOCAL VECTOR CHUNK STORAGE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            actions = {
                IconButton(onClick = { showAddPanel = !showAddPanel }) {
                    Icon(
                        if (showAddPanel) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Add Memory Node",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        // Visual Vector DB Status Bar
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
                        text = "VECTOR BASE INDEX STATUS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${nodes.size} CHUNKS SECURED",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Custom premium progress bar showing visual index usage
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color(0xFF1E293B), shape = RoundedCornerShape(3.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.35f) // simulated load factor
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                ),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }

                Text(
                    text = "Vector sync pipeline automatically indexes all files, text logs, and custom rules into Qdrant server-side for prompt routing.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    lineHeight = 14.sp
                )
            }
        }

        // Expandable input form to insert custom contexts directly
        AnimatedVisibility(
            visible = showAddPanel,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "ADD CORE SYSTEM CONTEXT NODE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = inputContent,
                        onValueChange = { inputContent = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("E.g., Slack channel for daily standup is #ops-standup") },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    )

                    // Selection Row for Category
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TYPE:",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        categories.forEach { cat ->
                            val isSelected = cat == selectedCategory
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f) else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (inputContent.isNotBlank()) {
                                viewModel.addMemory(inputContent, selectedCategory)
                                inputContent = ""
                                showAddPanel = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("INDEX CONTEXT", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Live search bar
        OutlinedTextField(
            value = searchState,
            onValueChange = { searchState = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search system knowledge bases...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedContainerColor = Color(0xFF0F111A),
                unfocusedContainerColor = Color(0xFF07080D)
            ),
            shape = RoundedCornerShape(24.dp)
        )

        // Knowledge Base Chunk List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (filteredNodes.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CloudQueue,
                            contentDescription = "Empty",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No Indexed Knowledge Found",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Try clearing search filters or add a custom rule.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            items(filteredNodes) { node ->
                MemoryNodeCard(node = node, onDelete = { viewModel.deleteMemory(node.id) })
            }
        }
    }
}

@Composable
fun MemoryNodeCard(node: MemoryNodeEntity, onDelete: () -> Unit) {
    val categoryColor = when (node.category) {
        "rule" -> MaterialTheme.colorScheme.secondary
        "document" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        when (node.category) {
                            "rule" -> Icons.Default.Gavel
                            "document" -> Icons.Default.Description
                            else -> Icons.Default.FactCheck
                        },
                        contentDescription = node.category,
                        tint = categoryColor,
                        modifier = Modifier.size(14.dp)
                    )
                    StatusBadge(text = node.category, color = categoryColor)
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Memory Node",
                        tint = Color(0xFFEF4444).copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = node.content,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Source: ${node.source}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Text(
                    text = "Indexed Vector: ${node.vectorId?.take(8) ?: "Local Cache"}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}
