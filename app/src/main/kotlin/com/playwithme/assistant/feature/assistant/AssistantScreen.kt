package com.playwithme.assistant.feature.assistant

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playwithme.assistant.core.common.toTimerDisplay
import com.playwithme.assistant.core.ui.theme.*
import com.playwithme.assistant.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    onBack: () -> Unit,
    viewModel: AssistantViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game Assistant", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    if (uiState.recentEvents.isNotEmpty()) {
                        IconButton(onClick = viewModel::clearAllEvents) {
                            Icon(Icons.Default.ClearAll, contentDescription = "Clear events", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
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
            // ─── Status Card ──────────────────────────────────────────────
            item { StatusCard(uiState) }

            // ─── Timer Section ────────────────────────────────────────────
            item {
                TimerSection(
                    activeTimers = uiState.activeTimers,
                    onStartTimer = viewModel::startTimer,
                    onDismissTimer = viewModel::dismissTimer
                )
            }

            // ─── Events Section ───────────────────────────────────────────
            if (uiState.recentEvents.isNotEmpty()) {
                item {
                    Text(
                        "Recent Events (${uiState.recentEvents.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                items(uiState.recentEvents, key = { it.id }) { event ->
                    EventCard(event = event, onDismiss = { viewModel.dismissEvent(event.id) })
                }
            } else {
                item { EmptyEventsState(uiState.isOcrEnabled) }
            }
        }
    }
}

@Composable
private fun StatusCard(uiState: AssistantUiState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatusIndicator("OCR", uiState.isOcrEnabled, uiState.ocrProcessingTimeMs.takeIf { it > 0 }?.let { "${it}ms" })
            VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            StatusIndicator("AI Analysis", uiState.isAiEnabled, null)
            VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            StatusIndicator("Events", true, "${uiState.recentEvents.size}")
        }
    }
}

@Composable
private fun StatusIndicator(label: String, active: Boolean, value: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(8.dp).clip(CircleShape)
                .background(if (active) GameGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (value != null) Text(value, style = MaterialTheme.typography.labelMedium, color = Primary60, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TimerSection(
    activeTimers: List<TimerEvent>,
    onStartTimer: (TimerType) -> Unit,
    onDismissTimer: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Objective Timers", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)

        // Quick start buttons
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf(TimerType.LORD to "👑 Lord", TimerType.TURTLE to "🐢 Turtle", TimerType.BUFF_ALLY to "💪 Buff")) { (type, label) ->
                OutlinedButton(
                    onClick = { onStartTimer(type) },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(label, style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Active timers
        activeTimers.forEach { timer ->
            ActiveTimerCard(timer = timer, onDismiss = { onDismissTimer(timer.id) })
        }
    }
}

@Composable
private fun ActiveTimerCard(timer: TimerEvent, onDismiss: () -> Unit) {
    val timerLabel = when (timer.type) {
        TimerType.LORD -> "👑 Lord"
        TimerType.TURTLE -> "🐢 Turtle"
        TimerType.BUFF_ALLY -> "💪 Ally Buff"
        TimerType.BUFF_ENEMY -> "⚠️ Enemy Buff"
        TimerType.CUSTOM -> "⏱ Custom"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(timerLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    timer.remainingSeconds.toTimerDisplay(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (timer.remainingSeconds <= 30) GameRed else Primary60,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                LinearProgressIndicator(
                    progress = { timer.progressFraction },
                    modifier = Modifier.width(100.dp).clip(RoundedCornerShape(4.dp)),
                    color = if (timer.remainingSeconds <= 30) GameRed else Primary60,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, "Dismiss", modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun EventCard(event: AnalysisResult, onDismiss: () -> Unit) {
    val priorityColor = when (event.priority) {
        SuggestionPriority.CRITICAL -> GameRed
        SuggestionPriority.HIGH -> Color(0xFFFF7043)
        SuggestionPriority.MEDIUM -> GameGold
        SuggestionPriority.LOW -> Neutral60
    }

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Priority indicator bar
                Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(priorityColor))

                Column(modifier = Modifier.weight(1f).padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            event.gameEvent.type.name.replace("_", " "),
                            style = MaterialTheme.typography.labelMedium,
                            color = priorityColor,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onDismiss, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, "Dismiss", modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(event.suggestion, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun EmptyEventsState(isOcrEnabled: Boolean) {
    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (isOcrEnabled) "👁" else "😴", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                if (isOcrEnabled) "Watching game screen..." else "OCR is disabled",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                if (isOcrEnabled) "Events will appear here" else "Enable OCR in Settings",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
