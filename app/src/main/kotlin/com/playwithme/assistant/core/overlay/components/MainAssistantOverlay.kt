package com.playwithme.assistant.core.overlay.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playwithme.assistant.core.ui.theme.*
import com.playwithme.assistant.domain.model.HeroCombo
import com.playwithme.assistant.domain.model.SkillKey
import com.playwithme.assistant.domain.model.ScreenState
import com.playwithme.assistant.domain.model.AppSettings
import com.playwithme.assistant.feature.draft.DraftState

enum class OverlayMode {
    LOBBY, DRAFT, IN_GAME, SCANNER
}

@Composable
fun MainAssistantOverlay(
    draftState: DraftState,
    screenState: ScreenState = ScreenState.UNKNOWN,
    appSettings: AppSettings = AppSettings(),
    onCloseClick: () -> Unit = {},
    onDrag: (dx: Float, dy: Float) -> Unit = { _, _ -> },
    onResize: (dw: Float, dh: Float) -> Unit = { _, _ -> },
    opacity: Float = 0.92f,
    onOpacityChange: (Float) -> Unit = {},
    fontScale: Float = 1.0f,
    onFontScaleChange: (Float) -> Unit = {},
    onToggleOcr: (Boolean) -> Unit = {},
    onToggleGankDetector: (Boolean) -> Unit = {},
    isAutoDetectEnabled: Boolean = false,
    onToggleAutoDetect: () -> Unit = {},
    onHeroSlotClick: (isAlly: Boolean, index: Int, isBan: Boolean) -> Unit = { _, _, _ -> },
    onScanClick: (com.playwithme.assistant.core.state.ScanCommand) -> Unit = {}
) {
    var mode by remember { mutableStateOf(OverlayMode.LOBBY) }
    var showSettings by remember { mutableStateOf(false) }

    // Auto switch mode based on screen state if AutoDetect is enabled
    LaunchedEffect(isAutoDetectEnabled, screenState) {
        if (isAutoDetectEnabled) {
            when (screenState) {
                ScreenState.LOBBY -> mode = OverlayMode.LOBBY
                ScreenState.DRAFT -> mode = OverlayMode.DRAFT
                ScreenState.IN_GAME -> mode = OverlayMode.IN_GAME
                ScreenState.SCANNING -> mode = OverlayMode.SCANNER
                ScreenState.UNKNOWN -> { /* Keep current mode if unknown */ }
            }
        }
    }

    // Dummy combos for testing
    val dummyCombos = remember {
        listOf(
            HeroCombo(
                "1", "chou", "Combo Kill Core", 
                listOf(
                    com.playwithme.assistant.domain.model.ComboStep(1, SkillKey.SKILL_1, ""),
                    com.playwithme.assistant.domain.model.ComboStep(2, SkillKey.SKILL_1, ""),
                    com.playwithme.assistant.domain.model.ComboStep(3, SkillKey.SKILL_2, ""),
                    com.playwithme.assistant.domain.model.ComboStep(4, SkillKey.SKILL_1, ""),
                    com.playwithme.assistant.domain.model.ComboStep(5, SkillKey.ULTIMATE, "")
                ),
                authorName = "TopGlobalChou", likes = 1420
            ),
            HeroCombo(
                "2", "gusion", "Fast Hand Burst", 
                listOf(
                    com.playwithme.assistant.domain.model.ComboStep(1, SkillKey.SKILL_2, ""),
                    com.playwithme.assistant.domain.model.ComboStep(2, SkillKey.ULTIMATE, ""),
                    com.playwithme.assistant.domain.model.ComboStep(3, SkillKey.SKILL_2, ""),
                    com.playwithme.assistant.domain.model.ComboStep(4, SkillKey.SKILL_1, ""),
                    com.playwithme.assistant.domain.model.ComboStep(5, SkillKey.SKILL_1, ""),
                    com.playwithme.assistant.domain.model.ComboStep(6, SkillKey.BASIC_ATTACK, "")
                ),
                authorName = "FastHand123", likes = 980
            ),
            HeroCombo(
                "3", "layla", "Sniper Setup", 
                listOf(
                    com.playwithme.assistant.domain.model.ComboStep(1, SkillKey.SKILL_1, ""),
                    com.playwithme.assistant.domain.model.ComboStep(2, SkillKey.BASIC_ATTACK, ""),
                    com.playwithme.assistant.domain.model.ComboStep(3, SkillKey.SKILL_2, ""),
                    com.playwithme.assistant.domain.model.ComboStep(4, SkillKey.ULTIMATE, "")
                ),
                authorName = "SniperGod", likes = 450
            )
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Mode Switcher
        if (!isAutoDetectEnabled) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Neutral20.copy(alpha = 0.8f))
                        .padding(2.dp)
                ) {
                    Row {
                        Button(
                            onClick = { mode = OverlayMode.LOBBY },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (mode == OverlayMode.LOBBY) GameGold else Color.Transparent
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text("Lobby", fontSize = 10.sp, color = if (mode == OverlayMode.LOBBY) Neutral10 else Neutral90)
                        }
                        Button(
                            onClick = { mode = OverlayMode.DRAFT },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (mode == OverlayMode.DRAFT) Primary60 else Color.Transparent
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text("Draft", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { mode = OverlayMode.IN_GAME },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (mode == OverlayMode.IN_GAME) GameRed else Color.Transparent
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text("In-Game", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { mode = OverlayMode.SCANNER },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (mode == OverlayMode.SCANNER) Primary40 else Color.Transparent
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text("Scanner", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
        
        // Settings Panel
        if (showSettings) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .weight(1f, fill = false),
                shape = RoundedCornerShape(12.dp),
                color = OverlayBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, OverlayBorder),
                shadowElevation = 8.dp
            ) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Settings Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Settings", color = Primary90, fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        IconButton(
                            onClick = { showSettings = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close Settings",
                                tint = Neutral70,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = NeutralVariant30.copy(alpha = 0.5f), thickness = 0.5.dp)

                    // Opacity Slider
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Opacity", color = Neutral90, fontSize = 11.sp, modifier = Modifier.weight(0.3f))
                        Slider(
                            value = opacity,
                            onValueChange = onOpacityChange,
                            valueRange = 0.2f..1.0f,
                            modifier = Modifier.weight(0.55f).padding(horizontal = 4.dp).height(24.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = Primary70, 
                                activeTrackColor = Primary60,
                                inactiveTrackColor = Neutral30
                            )
                        )
                        Text("${(opacity * 100).toInt()}%", color = Primary80, fontSize = 11.sp, modifier = Modifier.weight(0.15f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                    }
                    
                    // Font Size Slider
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Font Size", color = Neutral90, fontSize = 11.sp, modifier = Modifier.weight(0.3f))
                        Slider(
                            value = fontScale,
                            onValueChange = onFontScaleChange,
                            valueRange = 0.5f..1.5f,
                            modifier = Modifier.weight(0.55f).padding(horizontal = 4.dp).height(24.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = GameGold, 
                                activeTrackColor = Tertiary80,
                                inactiveTrackColor = Neutral30
                            )
                        )
                        Text("${(fontScale * 100).toInt()}%", color = GameGold, fontSize = 11.sp, modifier = Modifier.weight(0.15f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), thickness = 1.dp, color = Neutral30.copy(alpha = 0.5f))
                    Text("Advanced", color = Primary80, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

                    // OCR Toggle
                    Row(modifier = Modifier.fillMaxWidth().height(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Auto-Detect Mode (OCR)", color = Neutral90, fontSize = 11.sp)
                        Switch(
                            checked = appSettings.isOcrEnabled,
                            onCheckedChange = onToggleOcr,
                            modifier = Modifier.scale(0.7f),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Primary60,
                                uncheckedThumbColor = Neutral60,
                                uncheckedTrackColor = Neutral30
                            )
                        )
                    }

                    // Gank Detector Toggle
                    Row(modifier = Modifier.fillMaxWidth().height(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Minimap Gank Detector", color = Neutral90, fontSize = 11.sp)
                        Switch(
                            checked = appSettings.isGankDetectorEnabled,
                            onCheckedChange = onToggleGankDetector,
                            modifier = Modifier.scale(0.7f),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Primary60,
                                uncheckedThumbColor = Neutral60,
                                uncheckedTrackColor = Neutral30
                            )
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (mode) {
                OverlayMode.LOBBY -> {
                    LobbyOverlayPlaceholder(
                        onCloseClick = onCloseClick,
                        onSettingsClick = { showSettings = !showSettings },
                        onDrag = onDrag,
                        onResize = onResize,
                        opacity = opacity,
                        isAutoDetectEnabled = isAutoDetectEnabled,
                        onToggleAutoDetect = onToggleAutoDetect
                    )
                }
                OverlayMode.DRAFT -> {
                    DraftOverlayPlaceholder(
                        state = draftState,
                        onCloseClick = onCloseClick,
                        onSettingsClick = { showSettings = !showSettings },
                        onDrag = onDrag,
                        onResize = onResize,
                        opacity = opacity,
                        onToggleAutoDetect = onToggleAutoDetect,
                        onHeroSlotClick = onHeroSlotClick
                    )
                }
                OverlayMode.IN_GAME -> {
                    InGameOverlayPlaceholder(
                        combos = dummyCombos,
                        onCloseClick = onCloseClick,
                        onSettingsClick = { showSettings = !showSettings },
                        onDrag = onDrag,
                        onResize = onResize,
                        opacity = opacity,
                        fontScale = fontScale
                    )
                }
                OverlayMode.SCANNER -> {
                    ScannerOverlayPlaceholder(
                        onScanClick = onScanClick,
                        onCloseClick = { mode = OverlayMode.LOBBY },
                        opacity = opacity
                    )
                }
            }
        }
    }
}
