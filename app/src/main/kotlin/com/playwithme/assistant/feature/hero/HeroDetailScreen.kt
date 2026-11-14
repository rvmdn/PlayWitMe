package com.playwithme.assistant.feature.hero

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.playwithme.assistant.domain.model.*
import com.playwithme.assistant.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroDetailScreen(
    heroId: String,
    onBack: () -> Unit,
    viewModel: HeroDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(heroId) { viewModel.loadHero(heroId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.hero?.name ?: "Hero", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary60)
            }
            uiState.errorMessage != null -> ErrorState(uiState.errorMessage!!, onBack)
            uiState.hero != null -> HeroDetailContent(
                hero = uiState.hero!!,
                counterHeroes = uiState.counterHeroes,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun HeroDetailContent(
    hero: Hero,
    counterHeroes: List<Hero>,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Skills", "Build", "Combo")

    Column(modifier = modifier.fillMaxSize()) {
        // ─── Hero Header ──────────────────────────────────────────────────
        HeroHeader(hero)

        // ─── Tab Row ──────────────────────────────────────────────────────
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Primary60
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(title,
                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                            style = MaterialTheme.typography.labelLarge)
                    }
                )
            }
        }

        // ─── Tab Content ──────────────────────────────────────────────────
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> { // Overview
                    item { HeroOverviewSection(hero, counterHeroes) }
                }
                1 -> { // Skills
                    items(hero.skills) { skill ->
                        SkillCard(skill)
                    }
                }
                2 -> { // Build
                    items(hero.recommendedBuilds) { build ->
                        BuildCard(build)
                    }
                    item { EmblemCard(hero.recommendedEmblem, hero.recommendedSpells) }
                }
                3 -> { // Combo
                    if (hero.combos.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No combo guides yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(hero.combos) { combo ->
                            ComboCard(combo)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroHeader(hero: Hero) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(Brush.verticalGradient(listOf(Primary20, MaterialTheme.colorScheme.surface)))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hero icon
            Box(
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(Primary40, Secondary40))),
                contentAlignment = Alignment.Center
            ) {
                Text(hero.name.first().toString(), fontSize = 36.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(hero.name, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                Text(hero.title, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(hero.role) { role ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                role.name.lowercase().replaceFirstChar { it.uppercaseChar() },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroOverviewSection(hero: Hero, counterHeroes: List<Hero>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Description
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("About", style = MaterialTheme.typography.titleSmall, color = Primary60)
                Spacer(modifier = Modifier.height(4.dp))
                Text(hero.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Tips
        if (hero.tips.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💡 Tips", style = MaterialTheme.typography.titleSmall, color = Tertiary70)
                    Spacer(modifier = Modifier.height(8.dp))
                    hero.tips.forEach { tip ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text("•  ", color = Tertiary70)
                            Text(tip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Spells
        if (hero.recommendedSpells.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚡ Recommended Spells", style = MaterialTheme.typography.titleSmall, color = Secondary60)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(hero.recommendedSpells) { spell ->
                            Surface(shape = RoundedCornerShape(8.dp), color = Secondary30.copy(alpha = 0.3f)) {
                                Text(spell, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium, color = Secondary70)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillCard(skill: HeroSkill) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(8.dp), color = Primary40.copy(alpha = 0.2f)) {
                    Text(
                        skill.key.name.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary60,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(skill.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(skill.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (skill.cooldown.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("CD: ${skill.cooldown.joinToString("/")}s",
                    style = MaterialTheme.typography.labelSmall, color = Secondary60)
            }
        }
    }
}

@Composable
private fun BuildCard(build: HeroBuild) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Text(build.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Surface(shape = RoundedCornerShape(6.dp), color = phaseColor(build.phase).copy(alpha = 0.2f)) {
                    Text(build.phase.name, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall, color = phaseColor(build.phase))
                }
            }
            if (build.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(build.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(build.items) { item ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp)) {
                        Box(
                            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(listOf(Primary30, Secondary30))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.name.first().toString(), fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Text(item.name, style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
private fun EmblemCard(emblem: HeroEmblem, spells: List<String>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🏅 Emblem: ${emblem.name} (Tier ${emblem.tier})",
                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GameGold)
            if (emblem.talents.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                emblem.talents.forEach { talent ->
                    Text("• $talent", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ComboCard(combo: HeroCombo) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(combo.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface)
            Text(combo.description, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))
            combo.steps.sortedBy { it.order }.forEach { step ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Step number
                    Box(
                        modifier = Modifier.size(24.dp).clip(RoundedCornerShape(6.dp)).background(Primary40),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${step.order}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            step.skillKey.name.replace("_", " "),
                            style = MaterialTheme.typography.labelMedium,
                            color = Primary60,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (step.description.isNotBlank()) {
                            Text(step.description, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (step.order < combo.steps.size) {
                    Box(modifier = Modifier.padding(start = 11.dp).width(2.dp).height(8.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
                }
            }
            if (combo.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("📝 ${combo.notes}", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun ErrorState(message: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("❌", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) { Text("Go Back") }
        }
    }
}

@Composable
private fun phaseColor(phase: BuildPhase): Color = when (phase) {
    BuildPhase.EARLY -> Color(0xFF43A047)
    BuildPhase.MID -> Color(0xFFFFB300)
    BuildPhase.LATE -> Color(0xFFE53935)
    BuildPhase.COUNTER -> Color(0xFF8E24AA)
}
