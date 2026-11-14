package com.playwithme.assistant.core.overlay.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playwithme.assistant.core.ui.theme.*

@Composable
fun LobbyOverlayPlaceholder(
    onCloseClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onDrag: (dx: Float, dy: Float) -> Unit = { _, _ -> },
    onResize: (dw: Float, dh: Float) -> Unit = { _, _ -> },
    opacity: Float = 0.92f,
    isAutoDetectEnabled: Boolean = false,
    onToggleAutoDetect: () -> Unit = {}
) {
    var isCollapsed by remember { mutableStateOf(false) }
    
    // Dummy state for Daily Tracker
    var wins by remember { mutableStateOf(0) }
    var losses by remember { mutableStateOf(0) }

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
                    colors = listOf(Primary60.copy(alpha = 0.6f), Secondary60.copy(alpha = 0.4f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // ─── Header ───────────────────────────────────────────────
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DragIndicator,
                        contentDescription = "Drag",
                        tint = Neutral60,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Lobby Tracker",
                        color = Primary80,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Auto-detect toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleAutoDetect() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAutoDetectEnabled) "AUTO" else "MANUAL",
                            color = if (isAutoDetectEnabled) GameGreen else GameGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Neutral70,
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

            // ─── Content ─────────────────────────────────────────────────
            if (!isCollapsed) {
                HorizontalDivider(color = NeutralVariant30.copy(alpha = 0.5f), thickness = 0.5.dp)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    // Daily Tracker Section
                    Text("DAILY TRACKER", color = GameGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { wins++ },
                            colors = ButtonDefaults.buttonColors(containerColor = GameBlue.copy(alpha = 0.2f)),
                            border = BorderStroke(0.5.dp, GameBlue),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("+ WIN", color = GameBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$wins W - $losses L", color = Neutral90, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            val total = wins + losses
                            val winRate = if (total > 0) (wins.toFloat() / total * 100).toInt() else 0
                            Text("WR: $winRate%", color = Neutral60, fontSize = 10.sp)
                        }
                        
                        Button(
                            onClick = { losses++ },
                            colors = ButtonDefaults.buttonColors(containerColor = GameRed.copy(alpha = 0.2f)),
                            border = BorderStroke(0.5.dp, GameRed),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("+ LOSE", color = GameRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = NeutralVariant30.copy(alpha = 0.5f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Meta Tier List (Mock)
                    Text("META SEASON INI (S+ TIER)", color = GameGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Ling", "Fanny", "Nolan", "Joy").forEach { heroName ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NeutralVariant30)
                                        .border(1.dp, Primary60, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(heroName.take(1), color = Primary90, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(heroName, color = Neutral90, fontSize = 9.sp)
                            }
                        }
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
