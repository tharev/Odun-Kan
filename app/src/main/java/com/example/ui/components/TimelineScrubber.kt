package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun TimelineScrubber(
    currentDay: Int,
    onDayChange: (Int) -> Unit,
    saturation: Float,
    isTimelapsePlaying: Boolean,
    onToggleTimelapse: () -> Unit,
    onTriggerBloom: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val stage = WorldStage.fromDay(currentDay)

    val surfaceColor = if (isDark) Color(0xFF1A1D24) else Color(0xFFFAF8F3)
    val borderColor = if (isDark) Color(0xFF2C3240) else Color(0xFFE2DDD2)
    val textPrimary = if (isDark) Color(0xFFE9ECF2) else Color(0xFF1E232B)
    val textSecondary = if (isDark) Color(0xFF9098A8) else Color(0xFF6B665E)

    val stageColor by animateColorAsState(
        targetValue = stage.primaryPigment,
        label = "stage_color"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = surfaceColor,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Stage and Day Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(stageColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "STAGE ${stage.stageNumber}: ${stage.title.uppercase()}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "${stage.yorubaName} // DAYS ${stage.dayRange.first}–${stage.dayRange.last}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = textSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "DAY ${currentDay.toString().padStart(3, '0')}/365",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 365-Day Slider
            Slider(
                value = currentDay.toFloat(),
                onValueChange = { onDayChange(it.toInt().coerceIn(1, 365)) },
                valueRange = 1f..365f,
                colors = SliderDefaults.colors(
                    thumbColor = stageColor,
                    activeTrackColor = stageColor,
                    inactiveTrackColor = if (isDark) Color(0xFF2C3240) else Color(0xFFDDD8CE)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .testTag("day_slider")
            )

            // Milestone quick jump labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val milestones = listOf(
                    1 to "Cot",
                    90 to "Garden",
                    180 to "Hamlet",
                    270 to "City",
                    365 to "Year 1"
                )
                for ((day, label) in milestones) {
                    Text(
                        text = "D$day $label",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = if (currentDay >= day) stageColor else textSecondary,
                        modifier = Modifier.clickable { onDayChange(day) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Saturation Gauge & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Saturation Bar
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "SKETCHBOOK SATURATION",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textSecondary
                        )
                        Text(
                            text = "${(saturation * 100).toInt()}%",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = stageColor
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { saturation.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = stageColor,
                        trackColor = if (isDark) Color(0xFF282D37) else Color(0xFFE2DDD2)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Timelapse Play/Pause Button
                Surface(
                    onClick = onToggleTimelapse,
                    shape = RoundedCornerShape(8.dp),
                    color = if (isTimelapsePlaying) stageColor else (if (isDark) Color(0xFF282E3A) else Color(0xFFECE7DC)),
                    modifier = Modifier.testTag("timelapse_toggle_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isTimelapsePlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Timelapse",
                            tint = if (isTimelapsePlaying) Color.White else textPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isTimelapsePlaying) "Pause" else "Timelapse",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTimelapsePlaying) Color.White else textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Trigger Bloom Button
                Surface(
                    onClick = onTriggerBloom,
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) Color(0xFF253328) else Color(0xFFE2EFE5),
                    modifier = Modifier.testTag("trigger_bloom_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = "Bloom",
                            tint = Color(0xFF52B788),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bloom",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF52B788)
                        )
                    }
                }
            }
        }
    }
}
