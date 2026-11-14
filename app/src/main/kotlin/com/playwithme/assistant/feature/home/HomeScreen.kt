package com.playwithme.assistant.feature.home

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playwithme.assistant.core.permission.PermissionManager
import com.playwithme.assistant.core.ui.theme.*

/**
 * Home screen — Dashboard and quick access panel.
 *
 * Features:
 * - Permission status cards
 * - Overlay start/stop button
 * - Quick navigation tiles
 * - Hero database count
 */
@Composable
fun HomeScreen(
    onNavigateToHeroes: () -> Unit,
    onNavigateToAssistant: () -> Unit,
    onNavigateToBuild: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToOverlay: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToScannedHeroes: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.refreshPermissions() }

    Scaffold(
        topBar = { HomeTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ─── Hero Banner ──────────────────────────────────────────────
            HeroBannerCard()

            // ─── Permission Status ────────────────────────────────────────
            if (!uiState.permissionState.isComplete) {
                PermissionWarningCard(
                    missingPermissions = uiState.permissionState.missingPermissions,
                    onGrantOverlay = {
                        context.startActivity(
                            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}"))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                )
            }

            // ─── Overlay Control ──────────────────────────────────────────
            OverlayControlCard(
                isRunning = uiState.isOverlayRunning,
                hasPermission = uiState.permissionState.hasOverlayPermission,
                onStart = { 
                    val activity = context as? com.playwithme.assistant.app.MainActivity
                    activity?.requestMediaProjectionPermission()
                    viewModel.startOverlay() 
                },
                onStop = { viewModel.stopOverlay() },
                onConfigure = onNavigateToOverlay
            )

            // ─── Quick Actions Grid ───────────────────────────────────────
            Text(
                text = "Quick Access",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            QuickActionsGrid(
                heroCount = uiState.heroCount,
                onHeroes = onNavigateToHeroes,
                onAssistant = onNavigateToAssistant,
                onBuild = onNavigateToBuild,
                onAnalytics = onNavigateToAnalytics,
                onSettings = onNavigateToSettings,
                onScannedHeroes = onNavigateToScannedHeroes
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Play",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "WitMe",
                    style = MaterialTheme.typography.titleLarge,
                    color = Primary60,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun HeroBannerCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Primary30, Secondary30, Primary20)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    "Game Assistant AI",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Real-time overlay • Read-only • No automation",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            Text(
                "🎮",
                fontSize = 48.sp,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun PermissionWarningCard(
    missingPermissions: List<String>,
    onGrantOverlay: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null,
                    tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Permissions Required",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer)
            }
            Spacer(modifier = Modifier.height(8.dp))
            missingPermissions.forEach { perm ->
                Text("• $perm",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onGrantOverlay,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Grant Permissions")
            }
        }
    }
}

@Composable
private fun OverlayControlCard(
    isRunning: Boolean,
    hasPermission: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onConfigure: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "overlay_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse_alpha"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Overlay Service",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        if (isRunning) "Running" else if (hasPermission) "Ready to start" else "Permission required",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isRunning) GameGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status indicator
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRunning) GameGreen.copy(alpha = pulseAlpha * 0.2f)
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isRunning) Icons.Default.Layers else Icons.Outlined.Layers,
                        contentDescription = null,
                        tint = if (isRunning) GameGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = if (isRunning) onStop else onStart,
                    enabled = hasPermission,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) MaterialTheme.colorScheme.error else Primary50
                    )
                ) {
                    Icon(
                        if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isRunning) "Stop" else "Start")
                }

                OutlinedButton(onClick = onConfigure, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Configure")
                }
            }
        }
    }
}

@Composable
private fun QuickActionsGrid(
    heroCount: Int,
    onHeroes: () -> Unit,
    onAssistant: () -> Unit,
    onBuild: () -> Unit,
    onAnalytics: () -> Unit,
    onSettings: () -> Unit,
    onScannedHeroes: () -> Unit
) {
    val actions = listOf(
        QuickAction("Heroes", Icons.Default.Person, "${heroCount} heroes", onHeroes, Primary60),
        QuickAction("Assistant", Icons.Default.SmartToy, "AI Analysis", onAssistant, Secondary60),
        QuickAction("Builds", Icons.Default.Build, "Item builds", onBuild, Tertiary70),
        QuickAction("Analytics", Icons.Default.BarChart, "Game stats", onAnalytics, GameGold),
        QuickAction("Settings", Icons.Default.Settings, "Configure app", onSettings, Neutral60),
        QuickAction("Scanned DB", Icons.Default.CameraAlt, "Scan results", onScannedHeroes, Primary40)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        actions.chunked(2).forEach { rowActions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowActions.forEach { action ->
                    QuickActionCard(
                        action = action,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if odd number
                if (rowActions.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

private data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val subtitle: String,
    val onClick: () -> Unit,
    val accentColor: Color
)

@Composable
private fun QuickActionCard(action: QuickAction, modifier: Modifier = Modifier) {
    Card(
        onClick = action.onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                action.icon,
                contentDescription = null,
                tint = action.accentColor,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(action.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface)
                Text(action.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
