package com.playwithme.assistant.core.overlay.components

import androidx.compose.animation.core.*
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
import com.playwithme.assistant.domain.model.Hero
import com.playwithme.assistant.feature.draft.DraftState

@Composable
fun DraftOverlayPlaceholder(
    state: DraftState,
    onCloseClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onDrag: (dx: Float, dy: Float) -> Unit = { _, _ -> },
    onResize: (dw: Float, dh: Float) -> Unit = { _, _ -> },
    opacity: Float = 0.92f,
    onToggleAutoDetect: () -> Unit = {},
    onHeroSlotClick: (isAlly: Boolean, index: Int, isBan: Boolean) -> Unit = { _, _, _ -> }
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
                    colors = listOf(Secondary60.copy(alpha = 0.6f), Primary60.copy(alpha = 0.4f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        val isWideMode = maxWidth > 400.dp
        
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
                        text = "Draft Helper",
                        color = Secondary80,
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
                            text = if (state.isAutoDetectEnabled) "AUTO" else "MANUAL",
                            color = if (state.isAutoDetectEnabled) GameGreen else GameGold,
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

                if (isWideMode) {
                    // HORIZONTAL LAYOUT
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .padding(bottom = 16.dp) // space for resize handle
                    ) {
                        // Picks Section
                        Row(
                            modifier = Modifier.weight(2f),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ALLIES", color = GameBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row {
                                    state.alliedPicks.take(5).forEachIndexed { index, hero ->
                                        Box(modifier = Modifier.width(60.dp).padding(2.dp)) {
                                            HeroSlot(hero, onClick = { onHeroSlotClick(true, index, false) })
                                        }
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ENEMIES", color = GameRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row {
                                    state.enemyPicks.take(5).forEachIndexed { index, hero ->
                                        Box(modifier = Modifier.width(60.dp).padding(2.dp)) {
                                            HeroSlot(hero, isEnemy = true, onClick = { onHeroSlotClick(false, index, false) })
                                        }
                                    }
                                }
                            }
                        }

                        VerticalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = NeutralVariant30.copy(alpha = 0.5f))

                        // Recommendations
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("COUNTERS", color = GameGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            if (state.suggestedCounters.isEmpty()) {
                                Text(
                                    text = "Waiting...",
                                    color = Neutral60,
                                    fontSize = 11.sp,
                                )
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    state.suggestedCounters.take(3).forEach { hero ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(NeutralVariant30)
                                                    .border(1.dp, GameGold, RoundedCornerShape(8.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(hero.name.take(1), color = Primary90, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Text(hero.name, color = Neutral90, fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // VERTICAL LAYOUT
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .padding(bottom = 16.dp) // space for resize handle
                    ) {
                        // Picks Section
                        Row(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Allies Picks
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("ALLIES", color = GameBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                state.alliedPicks.take(5).forEachIndexed { index, hero ->
                                    HeroSlot(hero, onClick = { onHeroSlotClick(true, index, false) })
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }

                            // Enemies Picks
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("ENEMIES", color = GameRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                state.enemyPicks.take(5).forEachIndexed { index, hero ->
                                    HeroSlot(hero, isEnemy = true, onClick = { onHeroSlotClick(false, index, false) })
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = NeutralVariant30.copy(alpha = 0.5f), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(4.dp))

                        // Recommendations Section (Top 3)
                        Text("RECOMMENDED COUNTERS", color = GameGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        if (state.suggestedCounters.isEmpty()) {
                            Text(
                                text = "Waiting for enemy picks...",
                                color = Neutral60,
                                fontSize = 11.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                state.suggestedCounters.take(3).forEach { hero ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(NeutralVariant30)
                                                .border(1.dp, GameGold, RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(hero.name.take(1), color = Primary90, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(hero.name, color = Neutral90, fontSize = 10.sp)
                                    }
                                }
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

@Composable
private fun HeroSlot(hero: Hero?, isEnemy: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isEnemy) Error10.copy(alpha = 0.6f) else Secondary10.copy(alpha = 0.6f))
            .border(0.5.dp, if (isEnemy) GameRed.copy(alpha = 0.3f) else GameBlue.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (hero != null) {
            Text(hero.name, color = Neutral90, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        } else {
            Text("Empty", color = Neutral50, fontSize = 10.sp)
        }
    }
}
