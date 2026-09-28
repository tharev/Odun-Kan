package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RealisticStageArtCard
import com.example.ui.isometric.WorldStage
import com.google.firebase.auth.FirebaseUser

@Composable
fun VeoAnimateScreen(
    currentDay: Int,
    currentUser: FirebaseUser?,
    onSignInAnonymously: () -> Unit,
    onSignOut: () -> Unit,
    onGenerateVeoVideo: (Uri?, String, String) -> Unit,
    isGeneratingVideo: Boolean,
    videoGenerationProgress: Float,
    videoGenerationStatus: String,
    lastGeneratedVideoUri: String?,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    var selectedStage by remember { mutableStateOf(WorldStage.fromDay(currentDay)) }

    val textPrimary = if (isDark) Color(0xFFE9ECF2) else Color(0xFF1E232B)
    val textSecondary = if (isDark) Color(0xFF8F98A8) else Color(0xFF6B665E)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Auth & Cloud Sync Status Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF1E232B) else Color(0xFFFFFFFF)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) Color(0xFF2C3240) else Color(0xFFE2DDD2)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (currentUser != null) Color(0xFF52B788).copy(alpha = 0.2f)
                                    else if (isDark) Color(0xFF2C3240) else Color(0xFFE8E4DA)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentUser != null) Icons.Default.CloudDone else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (currentUser != null) Color(0xFF52B788) else textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (currentUser != null) "Cloud Sync Connected" else "Guest Mode (Local)",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = if (currentUser != null) "Firestore data persistence active" else "Connect Firebase to sync memories",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = textSecondary
                            )
                        }
                    }

                    if (currentUser == null) {
                        Button(
                            onClick = onSignInAnonymously,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52B788)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("sign_in_button")
                        ) {
                            Text(
                                text = "Connect Auth",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = onSignOut,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("sign_out_button")
                        ) {
                            Text(
                                text = "Sign Out",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Title Section
        item {
            Column {
                Text(
                    text = "VEO VIDEO GENERATION // 3.1 FAST",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF52B788)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Animate Images Into Video",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Upload a photo or choose an architectural growth stage. Veo will generate fluid video with realistic motion in 16:9 or 9:16 aspect ratio.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = textSecondary
                )
            }
        }

        // Growth Stage Selector
        item {
            Text(
                text = "SELECT GROWTH STAGE TO ANIMATE:",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            val stages = WorldStage.values()
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(stages) { stage ->
                    val isSelected = selectedStage == stage
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStage = stage },
                        label = {
                            Text(
                                text = "${stage.stageNumber}. ${stage.title}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = stage.primaryPigment.copy(alpha = 0.2f),
                            selectedLabelColor = stage.primaryPigment
                        )
                    )
                }
            }
        }

        // Realistic Stage Art Card with photo upload and Veo video generation
        item {
            RealisticStageArtCard(
                stage = selectedStage,
                currentDay = currentDay,
                onGenerateVeoVideo = onGenerateVeoVideo,
                isGeneratingVideo = isGeneratingVideo,
                videoGenerationProgress = videoGenerationProgress,
                videoGenerationStatus = videoGenerationStatus,
                lastGeneratedVideoUri = lastGeneratedVideoUri
            )
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}
