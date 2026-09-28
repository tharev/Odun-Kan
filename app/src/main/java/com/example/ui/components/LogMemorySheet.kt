package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.MemoryEntry

val PIGMENT_PALETTE = listOf(
    Pair("#52B788", "Olive Sage"),
    Pair("#E76F51", "Terracotta"),
    Pair("#E9C46A", "Ochre Gold"),
    Pair("#264653", "Indigo Dusk"),
    Pair("#D46A6A", "Rose Petal"),
    Pair("#48CAE4", "Canal Azure")
)

val MOOD_TAGS = listOf("Solitude", "Bloom", "Kinship", "Creation", "Insight", "Gratitude")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LogMemorySheet(
    initialDay: Int,
    initialPrompt: String = "",
    onDismiss: () -> Unit,
    onSaveMemory: (MemoryEntry) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var day by remember { mutableIntStateOf(initialDay) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("Bloom") }
    var selectedPigmentHex by remember { mutableStateOf("#52B788") }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var attachedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var generatedVideoUri by remember { mutableStateOf<String?>(null) }
    var isGeneratingVeo by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            attachedPhotoUri = uri
        }
    }

    val growthPhase = MemoryEntry.growthPhaseForDay(day)

    val bgColor = if (isDark) Color(0xFF1B1E26) else Color(0xFFFAF7F0)
    val textPrimary = if (isDark) Color(0xFFE9ECF2) else Color(0xFF1E232B)
    val textSecondary = if (isDark) Color(0xFF8F98A8) else Color(0xFF6B665E)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = bgColor,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ỌDÚN KAN // LOG MEMORY",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary
                    )
                    Text(
                        text = "Plant Day ${day.toString().padStart(3, '0')}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Growth Phase Badge (tracks cot to city)
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF52B788).copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF52B788).copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GROWTH_PHASE: ${growthPhase.uppercase()}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF52B788)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (growthPhase) {
                            "cot" -> "(Days 1–30: The Cot)"
                            "hearth" -> "(Days 31–90: The Hearth)"
                            "garden" -> "(Days 91–180: The Garden)"
                            "hamlet" -> "(Days 181–270: The Hamlet)"
                            else -> "(Days 271–365: The City)"
                        },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = textSecondary
                    )
                }
            }

            if (initialPrompt.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isDark) Color(0xFF242B36) else Color(0xFFEFEBE2),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Prompt: \"$initialPrompt\"",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = if (isDark) Color(0xFFF4A261) else Color(0xFFE76F51),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Day Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calendar Day (1–365):",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = textSecondary,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { if (day > 1) day-- },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF2C3240) else Color(0xFFE6E1D6)
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.size(36.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text("-", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = textPrimary)
                    }

                    Text(
                        text = " D$day ",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = textPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    Button(
                        onClick = { if (day < 365) day++ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF2C3240) else Color(0xFFE6E1D6)
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.size(36.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text("+", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = textPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Memory Title", fontFamily = FontFamily.Monospace, fontSize = 12.sp) },
                placeholder = { Text("e.g., The Cedar Wall & Rosemary Sprig", fontFamily = FontFamily.Monospace, fontSize = 12.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("memory_title_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF52B788),
                    unfocusedBorderColor = if (isDark) Color(0xFF384050) else Color(0xFFDDD8CE)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Content Input
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content / Notes & Reflections", fontFamily = FontFamily.Monospace, fontSize = 12.sp) },
                placeholder = { Text("Describe the light, the people, the space, or what quietly grew today...", fontFamily = FontFamily.Monospace, fontSize = 12.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("memory_content_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF52B788),
                    unfocusedBorderColor = if (isDark) Color(0xFF384050) else Color(0xFFDDD8CE)
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Photo Attachment Section
            if (attachedPhotoUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF52B788), RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = attachedPhotoUri,
                        contentDescription = "Attached photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                    IconButton(
                        onClick = { attachedPhotoUri = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove photo",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("attach_photo_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (attachedPhotoUri != null) "Change Photo" else "Attach Photo to Memory",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pigment Color
            Text(
                text = "WORLD PIGMENT UNLOCKED:",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for ((hex, _) in PIGMENT_PALETTE) {
                    val pColor = Color(android.graphics.Color.parseColor(hex))
                    val isSelected = selectedPigmentHex == hex
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(pColor)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) (if (isDark) Color.White else Color.Black) else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedPigmentHex = hex },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mood Tags
            Text(
                text = "MOOD THEME:",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (mood in MOOD_TAGS) {
                    val isSelected = selectedMood == mood
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedMood = mood },
                        label = {
                            Text(
                                text = mood,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isDark) Color(0xFF2C3E33) else Color(0xFFE2EFE7),
                            selectedLabelColor = Color(0xFF52B788)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = {
                    val entry = MemoryEntry(
                        dayNumber = day,
                        date = System.currentTimeMillis(),
                        content = content.ifBlank { "A quiet mark in the sketchbook of the year." },
                        growth_phase = growthPhase,
                        title = title.ifBlank { "Memory of Day $day" },
                        moodTag = selectedMood,
                        paletteColorHex = selectedPigmentHex,
                        tags = selectedTags.joinToString(", "),
                        promptQuestion = initialPrompt,
                        imageUrl = attachedPhotoUri?.toString(),
                        videoUrl = generatedVideoUri
                    )
                    onSaveMemory(entry)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_memory_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(android.graphics.Color.parseColor(selectedPigmentHex))
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Fold & Plant Memory (Bloom)",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
