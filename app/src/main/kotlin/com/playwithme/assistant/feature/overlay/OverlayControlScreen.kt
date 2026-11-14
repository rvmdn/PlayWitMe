package com.playwithme.assistant.feature.overlay

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playwithme.assistant.core.ui.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverlayControlScreen(
    onBack: () -> Unit,
    viewModel: OverlayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Overlay Control", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── Service Control ──────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Overlay Service", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = if (uiState.isOverlayRunning) viewModel::stopOverlay else viewModel::startOverlay,
                            enabled = uiState.hasPermission,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.isOverlayRunning) MaterialTheme.colorScheme.error else Primary60
                            )
                        ) {
                            Icon(if (uiState.isOverlayRunning) Icons.Default.Stop else Icons.Default.PlayArrow, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (uiState.isOverlayRunning) "Stop Overlay" else "Start Overlay")
                        }
                    }
                    if (!uiState.hasPermission) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("⚠️ Overlay permission required in Settings > Apps > Special access",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // ─── Appearance ───────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Appearance", style = MaterialTheme.typography.titleSmall, color = Primary60, fontWeight = FontWeight.SemiBold)

                    // Alpha slider
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Transparency", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                            Text("${(uiState.alpha * 100).roundToInt()}%", style = MaterialTheme.typography.labelMedium, color = Primary60)
                        }
                        Slider(
                            value = uiState.alpha,
                            onValueChange = viewModel::setAlpha,
                            valueRange = 0.1f..1.0f,
                            colors = SliderDefaults.colors(thumbColor = Primary60, activeTrackColor = Primary60)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Snap toggle
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Snap to Edge", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                            Text("Snap overlay when released near edges", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = uiState.snapEnabled,
                            onCheckedChange = viewModel::setSnap,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Primary60)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Lock toggle
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Lock Position", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                            Text("Prevent accidental movement", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = uiState.isLocked,
                            onCheckedChange = viewModel::setLocked,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Primary60)
                        )
                    }
                }
            }

            // ─── Info Card ────────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Primary30.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, null, tint = Primary60, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "The overlay runs on top of your game and provides informational assistance only. " +
                        "No automatic actions, clicks, or game inputs are performed.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
