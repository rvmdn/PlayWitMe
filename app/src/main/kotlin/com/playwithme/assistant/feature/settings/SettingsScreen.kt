package com.playwithme.assistant.feature.settings

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playwithme.assistant.core.ui.theme.*
import com.playwithme.assistant.domain.model.AiProvider
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings?") },
            text = { Text("All settings will be reset to default values.") },
            confirmButton = {
                TextButton(onClick = { viewModel.resetSettings(); showResetDialog = false }) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Default.RestartAlt, "Reset", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ─── Appearance ───────────────────────────────────────────────
            item { SettingsGroupTitle("🎨 Appearance") }
            item {
                SettingsCard {
                    SwitchRow(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Mode",
                        subtitle = "Use dark theme",
                        checked = settings.isDarkMode,
                        onCheckedChange = viewModel::setDarkMode
                    )
                }
            }

            // ─── Overlay ──────────────────────────────────────────────────
            item { SettingsGroupTitle("🪟 Overlay") }
            item {
                SettingsCard {
                    Column {
                        SliderRow(
                            icon = Icons.Default.Opacity,
                            title = "Transparency",
                            subtitle = "${(settings.overlayAlpha * 100).roundToInt()}%",
                            value = settings.overlayAlpha,
                            onValueChange = viewModel::setOverlayAlpha,
                            valueRange = 0.1f..1.0f
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        SwitchRow(
                            icon = Icons.Default.Fullscreen,
                            title = "Snap to Edge",
                            subtitle = "Snap overlay to screen edges",
                            checked = settings.overlaySnapEnabled,
                            onCheckedChange = viewModel::setOverlaySnap
                        )
                    }
                }
            }

            // ─── OCR ──────────────────────────────────────────────────────
            item { SettingsGroupTitle("👁 OCR & Capture") }
            item {
                SettingsCard {
                    Column {
                        SwitchRow(
                            icon = Icons.Default.TextFields,
                            title = "OCR Analysis",
                            subtitle = "Read game screen text",
                            checked = settings.isOcrEnabled,
                            onCheckedChange = viewModel::setOcrEnabled
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        SliderRow(
                            icon = Icons.Default.Speed,
                            title = "Capture Rate",
                            subtitle = "${settings.ocrFrameRateFps} fps",
                            value = settings.ocrFrameRateFps.toFloat(),
                            onValueChange = { viewModel.setOcrFrameRate(it.roundToInt()) },
                            valueRange = 1f..10f,
                            steps = 9
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        SliderRow(
                            icon = Icons.Default.HighQuality,
                            title = "Capture Quality",
                            subtitle = "${(settings.captureResolutionScale * 100).roundToInt()}%",
                            value = settings.captureResolutionScale,
                            onValueChange = viewModel::setCaptureScale,
                            valueRange = 0.25f..1.0f
                        )
                    }
                }
            }

            // ─── AI ───────────────────────────────────────────────────────
            item { SettingsGroupTitle("🤖 AI Analysis") }
            item {
                SettingsCard {
                    Column {
                        SwitchRow(
                            icon = Icons.Default.Psychology,
                            title = "AI Analysis",
                            subtitle = "Analyze detected game events",
                            checked = settings.isAiEnabled,
                            onCheckedChange = viewModel::setAiEnabled
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("AI Provider", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(8.dp))
                            AiProvider.values().forEach { provider ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = settings.aiProvider == provider,
                                        onClick = { viewModel.setAiProvider(provider) },
                                        colors = RadioButtonDefaults.colors(selectedColor = Primary60)
                                    )
                                    Column {
                                        Text(providerLabel(provider), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                                        Text(providerDesc(provider), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ─── Performance ──────────────────────────────────────────────
            item { SettingsGroupTitle("⚡ Performance") }
            item {
                SettingsCard {
                    SwitchRow(
                        icon = Icons.Default.BatteryChargingFull,
                        title = "Battery Optimization",
                        subtitle = "Reduce capture rate when battery low",
                        checked = settings.isBatteryOptimizationEnabled,
                        onCheckedChange = viewModel::setBatteryOptimization
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    "PlayWitMe — Game Assistant AI\nFor informational use only. No automation.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun SettingsGroupTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold,
        color = Primary60, modifier = Modifier.padding(vertical = 4.dp))
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) { content() }
}

@Composable
private fun SwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, null, tint = Primary60, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Primary60)
        )
    }
}

@Composable
private fun SliderRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Primary60, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Primary60)
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(thumbColor = Primary60, activeTrackColor = Primary60)
        )
    }
}

private fun providerLabel(provider: AiProvider) = when (provider) {
    AiProvider.RULE_BASED -> "Rule-Based (Offline)"
    AiProvider.GEMINI -> "Google Gemini"
    AiProvider.OPENAI -> "OpenAI GPT"
    AiProvider.OFFLINE_TF -> "Offline AI (TFLite)"
}

private fun providerDesc(provider: AiProvider) = when (provider) {
    AiProvider.RULE_BASED -> "Fast, always available, no internet needed"
    AiProvider.GEMINI -> "Advanced AI — requires API key + internet"
    AiProvider.OPENAI -> "GPT-powered — requires API key + internet"
    AiProvider.OFFLINE_TF -> "Local model — coming soon"
}
