package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.isometric.WorldStage

@Composable
fun YearMilestonesScreen(
    currentDay: Int,
    totalMemories: Int,
    saturation: Float,
    onSelectMilestoneDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val textPrimary = if (isDark) Color(0xFFE9ECF2) else Color(0xFF1E232B)
    val textSecondary = if (isDark) Color(0xFF8F98A8) else Color(0xFF6B665E)

    val stages = WorldStage.values().toList()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 0.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Overview card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF1E232B) else Color(0xFFFFFFFF),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) Color(0xFF2C3240) else Color(0xFFE2DDD2)
                ),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ỌDÚN KAN // 365-DAY CYCLE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF52B788)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "The Evolutionary Journey",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "In Yoruba, 'ọdún kan' signifies 'One Year'. Your space begins as a minimalist cot sketched in solitary graphite, blossoming into a fertile garden and a flourishing city.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = textSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "MEMORIES",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = textSecondary
                            )
                            Text(
                                text = "$totalMemories PLANTED",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        }

                        Column {
                            Text(
                                text = "WORLD VIBRANCY",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = textSecondary
                            )
                            Text(
                                text = "${(saturation * 100).toInt()}% SATURATION",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF52B788)
                            )
                        }

                        Column {
                            Text(
                                text = "CURRENT DAY",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = textSecondary
                            )
                            Text(
                                text = "DAY $currentDay/365",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "5 MILESTONE CHAPTERS",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(stages) { stage ->
            val isUnlocked = currentDay >= stage.dayRange.first
            val isCurrent = currentDay in stage.dayRange

            MilestoneCard(
                stage = stage,
                isUnlocked = isUnlocked,
                isCurrent = isCurrent,
                onClick = { onSelectMilestoneDay(stage.dayRange.first) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun MilestoneCard(
    stage: WorldStage,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF1E232B) else Color(0xFFFFFFFF)
    val cardBorder = if (isCurrent) stage.primaryPigment
                     else if (isDark) Color(0xFF2C3240)
                     else Color(0xFFE2DDD2)
    val textPrimary = if (isDark) Color(0xFFE9ECF2) else Color(0xFF1E232B)
    val textSecondary = if (isDark) Color(0xFF8F98A8) else Color(0xFF6B665E)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(if (isCurrent) 2.dp else 1.dp, cardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("milestone_card_${stage.stageNumber}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(stage.primaryPigment)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "STAGE ${stage.stageNumber} // DAYS ${stage.dayRange.first}–${stage.dayRange.last}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = stage.primaryPigment
                    )
                }

                if (isCurrent) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = stage.primaryPigment.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "ACTIVE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = stage.primaryPigment,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Unlocked",
                        tint = Color(0xFF52B788),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${stage.title} (${stage.yorubaName})",
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stage.description,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = textSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inspect in Sketchbook",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = stage.primaryPigment
                )
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.NavigateNext,
                    contentDescription = null,
                    tint = stage.primaryPigment,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
