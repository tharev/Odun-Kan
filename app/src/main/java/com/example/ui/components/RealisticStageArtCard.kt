package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.isometric.WorldStage
import kotlin.math.sin

@Composable
fun RealisticStageArtCard(
    stage: WorldStage,
    currentDay: Int,
    onGenerateVeoVideo: (Uri?, String, String) -> Unit,
    isGeneratingVideo: Boolean = false,
    videoGenerationProgress: Float = 0f,
    videoGenerationStatus: String = "",
    lastGeneratedVideoUri: String? = null,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedAspectRatio by remember { mutableStateOf("9:16") } // "9:16" or "16:9"

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    // Subtle realistic sunlight shimmer / breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer_anim")
    val shimmerPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "light_shimmer"
    )

    val cardBg = if (isDark) Color(0xFF1E232B) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF2C3240) else Color(0xFFE2DDD2)
    val textPrimary = if (isDark) Color(0xFFE9ECF2) else Color(0xFF1E232B)
    val textSecondary = if (isDark) Color(0xFF8F98A8) else Color(0xFF6B665E)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, cardBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Stage Title & Growth Phase Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(stage.primaryPigment)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "STAGE ${stage.stageNumber} // ${stage.title.uppercase()}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = stage.primaryPigment
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = stage.primaryPigment.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "GROWTH_PHASE: ${when (stage) {
                            WorldStage.THE_COT -> "COT"
                            WorldStage.THE_HEARTH -> "HEARTH"
                            WorldStage.THE_GARDEN -> "GARDEN"
                            WorldStage.THE_HAMLET -> "HAMLET"
                            WorldStage.THE_CITY -> "CITY"
                        }}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = stage.primaryPigment,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Realistic Visual Display Canvas / Video Frame
            val artAspectRatio = if (selectedAspectRatio == "16:9") (16f / 9f) else (9f / 16f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (selectedAspectRatio == "16:9") 190.dp else 240.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                stage.primaryPigment.copy(alpha = 0.25f),
                                if (isDark) Color(0xFF14171C) else Color(0xFFEBE6DC)
                            )
                        )
                    )
                    .border(1.dp, stage.primaryPigment.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Uploaded Memory Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                val sway = sin(shimmerPhase) * 1.5f
                                translationY = sway
                            }
                    )
                } else {
                    // Realistic Architectural Stage Artwork Display with gentle breathing shimmer
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = stage.primaryPigment,
                            modifier = Modifier
                                .size(36.dp)
                                .graphicsLayer {
                                    scaleX = 1f + sin(shimmerPhase) * 0.05f
                                    scaleY = 1f + sin(shimmerPhase) * 0.05f
                                }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${stage.title} (${stage.yorubaName})",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "Realistic Visual Layer // Days ${stage.dayRange.first}–${stage.dayRange.last}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = textSecondary
                        )
                    }
                }

                // Shimmering ambient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = (sin(shimmerPhase) * 0.04f).coerceAtLeast(0f)),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Generating Overlay
                if (isGeneratingVideo) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.65f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = stage.primaryPigment,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Veo 3.1 Fast Generating...",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = videoGenerationStatus,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFFDDD8CE)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { videoGenerationProgress },
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = stage.primaryPigment
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Aspect ratio toggle (16:9 landscape vs 9:16 portrait)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VEO ASPECT RATIO:",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = textSecondary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedAspectRatio == "9:16",
                        onClick = { selectedAspectRatio = "9:16" },
                        label = { Text("9:16 Portrait", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = stage.primaryPigment.copy(alpha = 0.2f),
                            selectedLabelColor = stage.primaryPigment
                        ),
                        modifier = Modifier.testTag("veo_aspect_9_16")
                    )

                    FilterChip(
                        selected = selectedAspectRatio == "16:9",
                        onClick = { selectedAspectRatio = "16:9" },
                        label = { Text("16:9 Landscape", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = stage.primaryPigment.copy(alpha = 0.2f),
                            selectedLabelColor = stage.primaryPigment
                        ),
                        modifier = Modifier.testTag("veo_aspect_16_9")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions: Upload Photo & Animate into Video (Veo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("upload_photo_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Upload Photo",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedImageUri != null) "Change Photo" else "Upload Photo",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = {
                        val prompt = "Cinematic slow camera pan of ${stage.title}, ${stage.description}, gentle wind swaying foliage, warm sunlight streaming, realistic sketchbook texture, 4k fluid motion"
                        onGenerateVeoVideo(selectedImageUri, prompt, selectedAspectRatio)
                    },
                    enabled = !isGeneratingVideo,
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("generate_veo_video_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = stage.primaryPigment)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Animate with Veo",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Animate Video (Veo)",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
