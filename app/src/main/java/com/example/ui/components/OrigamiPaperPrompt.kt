package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val PROMPTS_LIST = listOf(
    "What brought unexpected warmth to your space today?",
    "Which color or shadow defined your morning?",
    "What small seed did you plant or tend to?",
    "Who walked into your thoughts or shared your path?",
    "What quiet silence or background melody stayed with you?",
    "What architectural detail of your day would you draw in ink?",
    "What did you let go of to give yourself room to breathe?"
)

/**
 * An origami paper-folding prompt card that pops up with a 3D fold unfolding animation.
 */
@Composable
fun OrigamiPaperPrompt(
    dayNumber: Int,
    onPromptClicked: (String) -> Unit,
    onWriteMemoryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var promptIndex by remember { mutableIntStateOf((dayNumber - 1).coerceAtLeast(0) % PROMPTS_LIST.size) }

    // 3D Origami Unfold Angle: starts at 85 degrees (folded back) and springs to 0 (flat)
    val foldAngle = remember { Animatable(80f) }
    val foldAlpha = remember { Animatable(0.2f) }

    LaunchedEffect(promptIndex) {
        foldAngle.snapTo(80f)
        foldAlpha.snapTo(0.2f)
        foldAngle.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        foldAlpha.animateTo(1f)
    }

    val paperColor = if (isDark) Color(0xFF1E232B) else Color(0xFFFCFAF5)
    val creaseColor = if (isDark) Color(0xFF14181E) else Color(0xFFEBE6DC)
    val inkColor = if (isDark) Color(0xFFE4E7ED) else Color(0xFF22262E)
    val subInkColor = if (isDark) Color(0xFF98A0AF) else Color(0xFF6A655C)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .graphicsLayer {
                rotationX = foldAngle.value
                alpha = foldAlpha.value
                cameraDistance = 18f * density
                transformOrigin = TransformOrigin(0.5f, 0f)
            }
            .shadow(6.dp, RoundedCornerShape(10.dp))
            .background(paperColor, RoundedCornerShape(10.dp))
            .border(1.dp, if (isDark) Color(0xFF333A48) else Color(0xFFDFD9CD), RoundedCornerShape(10.dp))
            .clickable { onPromptClicked(PROMPTS_LIST[promptIndex]) }
            .testTag("origami_prompt_card")
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Origami Prompt",
                        tint = if (isDark) Color(0xFFF4A261) else Color(0xFFE76F51),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ỌJỌ́ ${dayNumber.toString().padStart(3, '0')} // REFLECTION",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = subInkColor,
                        letterSpacing = 0.5.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            promptIndex = (promptIndex + 1) % PROMPTS_LIST.size
                        },
                        modifier = Modifier.size(28.dp).testTag("cycle_prompt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "New Prompt",
                            tint = subInkColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // The folded crease line across the center of the origami paper
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(creaseColor)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\"${PROMPTS_LIST[promptIndex]}\"",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 19.sp,
                color = inkColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap to answer this fold",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = subInkColor
                )

                Surface(
                    onClick = onWriteMemoryClicked,
                    shape = RoundedCornerShape(6.dp),
                    color = if (isDark) Color(0xFF2C3442) else Color(0xFFECE7DC),
                    modifier = Modifier.testTag("write_memory_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = inkColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Log Memory",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = inkColor
                        )
                    }
                }
            }
        }
    }
}
