package com.playwithme.assistant.core.overlay.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playwithme.assistant.core.ui.theme.*
import com.playwithme.assistant.domain.model.HeroCombo
import com.playwithme.assistant.domain.model.SkillKey

@Composable
fun ComboDisplayList(combos: List<HeroCombo>, fontScale: Float = 1.0f, modifier: Modifier = Modifier) {
    if (combos.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No combos available", color = Neutral60, fontSize = (12 * fontScale).sp)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(combos.size) { index ->
                ComboItem(combos[index], fontScale)
            }
        }
    }
}

@Composable
private fun ComboItem(combo: HeroCombo, fontScale: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(NeutralVariant10.copy(alpha = 0.5f))
            .border(0.5.dp, NeutralVariant30, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "By ${combo.authorName}",
                color = GameGold,
                fontSize = (10 * fontScale).sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = "Likes", tint = GameRed, modifier = Modifier.size((12 * fontScale).dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = "${combo.likes}", color = Neutral80, fontSize = (10 * fontScale).sp)
            }
        }
        
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(combo.steps.size) { index ->
                val step = combo.steps[index]
                SkillIcon(step.skillKey, fontScale)
                
                if (index < combo.steps.size - 1) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Neutral50,
                        modifier = Modifier.size((12 * fontScale).dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SkillIcon(action: SkillKey, fontScale: Float) {
    val (abbr, color) = when (action) {
        SkillKey.PASSIVE -> "P" to GameBlue
        SkillKey.SKILL_1 -> "S1" to GameBlue
        SkillKey.SKILL_2 -> "S2" to GameBlue
        SkillKey.SKILL_3 -> "S3" to GameBlue
        SkillKey.ULTIMATE -> "ULT" to GameRed
        SkillKey.BASIC_ATTACK -> "BA" to GameGold
        SkillKey.BATTLE_SPELL -> "BS" to Primary60
    }
    
    Box(
        modifier = Modifier
            .size((24 * fontScale).dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(abbr, color = Neutral90, fontSize = (9 * fontScale).sp, fontWeight = FontWeight.Bold)
    }
}
