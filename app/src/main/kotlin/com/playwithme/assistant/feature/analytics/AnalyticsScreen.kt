package com.playwithme.assistant.feature.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.playwithme.assistant.core.ui.theme.*

/**
 * Analytics screen — placeholder for future match analysis features.
 * Shows detected event statistics and usage analytics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Session Stats
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().background(
                            Brush.linearGradient(listOf(Primary30, Secondary30)),
                            RoundedCornerShape(16.dp)
                        ).padding(20.dp)
                    ) {
                        Column {
                            Text("Current Session", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                StatItem("Events Detected", "0", Color.White)
                                StatItem("OCR Analyses", "0", Color.White)
                                StatItem("AI Suggestions", "0", Color.White)
                            }
                        }
                    }
                }
            }

            // Coming Soon card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📊", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Match Analytics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Detailed match history, win rate analysis, hero performance tracking, " +
                            "and AI-powered improvement suggestions are coming in a future update.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(shape = RoundedCornerShape(8.dp), color = Primary40.copy(alpha = 0.15f)) {
                            Text("Coming Soon", modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium, color = Primary60, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Planned Features
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🗺️ Planned Features", style = MaterialTheme.typography.titleSmall, color = Primary60, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        listOf(
                            "📈 Match history tracking",
                            "🏆 Win rate by hero",
                            "⏱ Objective timing analysis",
                            "🤖 AI Coach recommendations",
                            "☁️ Cloud sync across devices",
                            "🎯 Hero recommendation system"
                        ).forEach { feature ->
                            Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                Text(feature, style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = textColor, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.8f))
    }
}
