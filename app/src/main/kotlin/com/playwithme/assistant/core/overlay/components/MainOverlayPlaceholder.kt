package com.playwithme.assistant.core.overlay.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playwithme.assistant.core.ui.theme.*

/**
 * Placeholder overlay composable shown by [OverlayService] until the
 * full assistant content is loaded.
 *
 * Demonstrates drag handle, collapse, settings (opacity), close, and styling.
 */
@Composable
fun MainOverlayPlaceholder(
    onCloseClick: () -> Unit = {},
    onDrag: (dx: Float, dy: Float) -> Unit = { _, _ -> },
    opacity: Float = 0.92f,
    onOpacityChange: (Float) -> Unit = {}
) {
    var isCollapsed by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    val height by animateDpAsState(
        targetValue = if (isCollapsed) 44.dp else if (showSettings) 260.dp else 200.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "overlay_height"
    )

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xE01A1A2E),
                        Color(0xE00D0D1A)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Primary60.copy(alpha = 0.5f), Secondary60.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ─── Minimalist Title Bar ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 8.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onDrag(dragAmount.x, dragAmount.y)
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Drag handle icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DragIndicator,
                        contentDescription = "Drag",
                        tint = Neutral60,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PlayWitMe",
                        color = Primary80,
                        fontSize = 13.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showSettings = !showSettings },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (showSettings) Primary60 else Neutral70,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { isCollapsed = !isCollapsed },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Minimize,
                            contentDescription = if (isCollapsed) "Expand" else "Collapse",
                            tint = Neutral70,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onCloseClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Error80,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // ─── Settings Panel ──────────────────────────────────────────
            if (showSettings && !isCollapsed) {
                HorizontalDivider(color = NeutralVariant30.copy(alpha = 0.5f), thickness = 0.5.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Opacity", color = Neutral80, fontSize = 12.sp)
                        Text("${(opacity * 100).toInt()}%", color = Primary80, fontSize = 12.sp)
                    }
                    Slider(
                        value = opacity,
                        onValueChange = onOpacityChange,
                        valueRange = 0.2f..1.0f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Primary60,
                            activeTrackColor = Primary80,
                            inactiveTrackColor = NeutralVariant30
                        )
                    )
                }
            }

            // ─── Content ─────────────────────────────────────────────────
            if (!isCollapsed) {
                HorizontalDivider(color = NeutralVariant30.copy(alpha = 0.5f), thickness = 0.5.dp)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎮",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Assistant Ready",
                            color = Neutral80,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Start game analysis",
                            color = Neutral60,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
