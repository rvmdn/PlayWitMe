package com.playwithme.assistant.feature.hero

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
import com.playwithme.assistant.domain.model.*
import com.playwithme.assistant.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroListScreen(
    onHeroClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: HeroListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hero Database", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ─── Search Bar ───────────────────────────────────────────────
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onClear = viewModel::clearSearch,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // ─── Role Filter ──────────────────────────────────────────────
            RoleFilterRow(
                selectedRole = uiState.selectedRole,
                onRoleSelected = viewModel::onRoleSelected
            )

            // ─── Hero Count ───────────────────────────────────────────────
            if (uiState.filteredHeroes.isNotEmpty()) {
                Text(
                    "${uiState.filteredHeroes.size} heroes",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // ─── Hero List ────────────────────────────────────────────────
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary60)
                }
            } else if (uiState.filteredHeroes.isEmpty()) {
                EmptyHeroState(uiState.searchQuery)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.filteredHeroes, key = { it.id }) { hero ->
                        HeroListItem(hero = hero, onClick = { onHeroClick(hero.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search hero name...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            AnimatedVisibility(visible = query.isNotBlank()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary60,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
}

@Composable
private fun RoleFilterRow(
    selectedRole: HeroRole?,
    onRoleSelected: (HeroRole?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedRole == null,
                onClick = { onRoleSelected(null) },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary40,
                    selectedLabelColor = Color.White
                )
            )
        }
        items(HeroRole.values()) { role ->
            FilterChip(
                selected = selectedRole == role,
                onClick = { onRoleSelected(if (selectedRole == role) null else role) },
                label = { Text(role.name.titleCase()) },
                leadingIcon = {
                    Text(roleEmoji(role), fontSize = 12.sp)
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary40,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun HeroListItem(hero: Hero, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hero icon placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Primary30, Primary20)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(hero.name.first().toString(), fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(hero.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                Text(hero.title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    hero.role.take(2).forEach { role ->
                        RoleChip(role)
                    }
                }
            }

            // Difficulty indicator
            DifficultyDots(hero.difficulty)

            Icon(Icons.Default.ChevronRight, null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun RoleChip(role: HeroRole) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = roleColor(role).copy(alpha = 0.15f)
    ) {
        Text(
            "${roleEmoji(role)} ${role.name.lowercase().replaceFirstChar { it.uppercaseChar() }}",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = roleColor(role)
        )
    }
}

@Composable
private fun DifficultyDots(difficulty: HeroDifficulty) {
    val filled = when (difficulty) {
        HeroDifficulty.EASY -> 1
        HeroDifficulty.MODERATE -> 2
        HeroDifficulty.HARD -> 3
        HeroDifficulty.VERY_HARD -> 4
    }
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.padding(end = 8.dp)) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (index < filled) GameGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )
        }
    }
}

@Composable
private fun EmptyHeroState(query: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                if (query.isNotBlank()) "No hero found for \"$query\"" else "No heroes available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────
private fun String.titleCase() = split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }

private fun roleEmoji(role: HeroRole) = when (role) {
    HeroRole.FIGHTER -> "⚔️"
    HeroRole.MAGE -> "🔮"
    HeroRole.MARKSMAN -> "🏹"
    HeroRole.ASSASSIN -> "🗡️"
    HeroRole.SUPPORT -> "💚"
    HeroRole.TANK -> "🛡️"
}

@Composable
private fun roleColor(role: HeroRole): Color = when (role) {
    HeroRole.FIGHTER -> Color(0xFFFF7043)
    HeroRole.MAGE -> Color(0xFF7C4DFF)
    HeroRole.MARKSMAN -> Color(0xFF43A047)
    HeroRole.ASSASSIN -> Color(0xFF546E7A)
    HeroRole.SUPPORT -> Color(0xFF00ACC1)
    HeroRole.TANK -> Color(0xFF1E88E5)
}
