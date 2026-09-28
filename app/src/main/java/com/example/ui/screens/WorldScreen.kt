package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.components.OrigamiPaperPrompt
import com.example.ui.components.TimelineScrubber
import com.example.ui.isometric.IsometricSketchbookCanvas
import com.example.ui.isometric.WorldStage

@Composable
fun WorldScreen(
    currentDay: Int,
    saturation: Float,
    isTimelapsePlaying: Boolean,
    bloomTrigger: Boolean,
    inspectedElementTitle: String?,
    onDayChange: (Int) -> Unit,
    onToggleTimelapse: () -> Unit,
    onTriggerBloom: () -> Unit,
    onBloomFinished: () -> Unit,
    onElementSelected: (String) -> Unit,
    onDismissElement: () -> Unit,
    onOpenLogMemory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val stage = WorldStage.fromDay(currentDay)

    val surfaceBg = if (isDark) Color(0xFF14171C) else Color(0xFFF9F7F2)
    val textPrimary = if (isDark) Color(0xFFE9ECF2) else Color(0xFF1E232B)
    val textSecondary = if (isDark) Color(0xFF8F98A8) else Color(0xFF6B665E)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceBg)
    ) {
        // 1. Core Isometric Sketchbook Canvas
        IsometricSketchbookCanvas(
            dayNumber = currentDay,
            saturation = saturation,
            bloomTrigger = bloomTrigger,
            onBloomFinished = onBloomFinished,
            onElementSelected = onElementSelected,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Top Header & Origami Prompt Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .align(Alignment.TopCenter)
        ) {
            // App Title & Current Stage Chip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ọdún kan",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Isometric Sketchbook Memory Journal",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = textSecondary
                    )
                }

                // Stage Chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDark) Color(0xFF1E232B) else Color(0xFFEBE6DC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, stage.primaryPigment)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(stage.primaryPigment)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stage.yorubaName,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = stage.primaryPigment
                        )
                    }
                }
            }

            // Origami Paper Prompt Card
            OrigamiPaperPrompt(
                dayNumber = currentDay,
                onPromptClicked = { prompt ->
                    onOpenLogMemory(prompt)
                },
                onWriteMemoryClicked = {
                    onOpenLogMemory("")
                }
            )
        }

        // 3. Inspected Element Pill (if user tapped on an element in the world)
        AnimatedVisibility(
            visible = inspectedElementTitle != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 120.dp)
        ) {
            inspectedElementTitle?.let { title ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) Color(0xFF1E232B) else Color(0xFFFFFFFF),
                    shadowElevation = 6.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, stage.primaryPigment)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = stage.primaryPigment,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Inspecting: $title",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(
                            onClick = onDismissElement,
                            shape = CircleShape,
                            color = if (isDark) Color(0xFF2C3240) else Color(0xFFECE7DC)
                        ) {
                            Text(
                                text = "✕",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = textSecondary
                            )
                        }
                    }
                }
            }
        }

        // 4. Bottom Controls: Timeline Scrubber & FAB
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            TimelineScrubber(
                currentDay = currentDay,
                onDayChange = onDayChange,
                saturation = saturation,
                isTimelapsePlaying = isTimelapsePlaying,
                onToggleTimelapse = onToggleTimelapse,
                onTriggerBloom = onTriggerBloom
            )
            Spacer(modifier = Modifier.height(56.dp)) // Leave room for bottom bar
        }

        // Floating Action Button to plant memory
        FloatingActionButton(
            onClick = { onOpenLogMemory("") },
            containerColor = Color(0xFF52B788),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 125.dp)
                .testTag("fab_log_memory")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Log Memory"
            )
        }
    }
}
