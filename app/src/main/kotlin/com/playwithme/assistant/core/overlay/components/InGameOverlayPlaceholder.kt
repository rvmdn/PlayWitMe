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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playwithme.assistant.core.ui.theme.*
import com.playwithme.assistant.domain.model.HeroCombo

@Composable
fun InGameOverlayPlaceholder(
    combos: List<HeroCombo>,
    onCloseClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onDrag: (dx: Float, dy: Float) -> Unit = { _, _ -> },
    onResize: (dw: Float, dh: Float) -> Unit = { _, _ -> },
    opacity: Float = 0.92f,
    fontScale: Float = 1.0f
) {
    var isCollapsed by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
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
                    colors = listOf(GameBlue.copy(alpha = 0.6f), Primary60.copy(alpha = 0.4f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        val isWideMode = maxWidth > 300.dp
        
        Column(modifier = Modifier.fillMaxSize()) {
            
            // ─── Header ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DragIndicator,
                        contentDescription = "Drag",
                        tint = Neutral60,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "In-Game Combos",
                        color = GameBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Neutral70,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(
                        onClick = { isCollapsed = !isCollapsed },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Minimize,
                            contentDescription = if (isCollapsed) "Expand" else "Collapse",
                            tint = Neutral70,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(
                        onClick = onCloseClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Error80,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // ─── Content ─────────────────────────────────────────────────
            if (!isCollapsed) {
                HorizontalDivider(color = NeutralVariant30.copy(alpha = 0.5f), thickness = 0.5.dp)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .padding(bottom = 12.dp) // space for resize handle
                ) {
                    if (isWideMode) {
                        // In wide mode, maybe show combos horizontally or grid
                        ComboDisplayList(combos, fontScale = fontScale, modifier = Modifier.fillMaxSize())
                    } else {
                        // In narrow mode, list vertically
                        ComboDisplayList(combos, fontScale = fontScale, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
        
        // ─── Resize Handle ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(24.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onResize(dragAmount.x, dragAmount.y)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.DragIndicator,
                contentDescription = "Resize",
                tint = Neutral50,
                modifier = Modifier
                    .size(16.dp)
                    .rotate(45f)
            )
        }
    }
}
