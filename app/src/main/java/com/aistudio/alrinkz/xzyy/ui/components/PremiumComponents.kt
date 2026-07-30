package com.aistudio.alrinkz.xzyy.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Premium Glassmorphic Container with linear gradient border
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
    cornerRadius: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp)
    ) {
        content()
    }
}

// 2. Active Pulse Core Indicator
@Composable
fun ActivePulse(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val opacity by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "opacity"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(24.dp)
    ) {
        // Pulse ring
        Box(
            modifier = Modifier
                .size(16.dp)
                .alpha(opacity)
                .drawBehind {
                    drawCircle(
                        color = color,
                        radius = (size.minDimension / 2) * scale
                    )
                }
        )
        // Solid core
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
    }
}

// 3. Code-like Terminal Line with subtle gradient bg
@Composable
fun TerminalLogLine(
    text: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    label: String = "ALRIN:~$ "
) {
    val textColor = if (isError) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF070913), shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = textColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

// 4. Custom status badge
@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Text(
            text = text.uppercase(),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            letterSpacing = 1.sp
        )
    }
}

// Draw a line connecting nodes visually
fun Modifier.drawNodeConnector(
    strokeWidth: Float = 4f,
    color: Color
) = this.drawBehind {
    val startY = 0f
    val endY = size.height
    val x = 12.dp.toPx() // Adjust to match avatar center

    drawLine(
        color = color,
        start = Offset(x, startY),
        end = Offset(x, endY),
        strokeWidth = strokeWidth
    )
}
