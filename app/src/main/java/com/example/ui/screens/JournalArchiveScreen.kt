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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.MemoryEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalArchiveScreen(
    memories: List<MemoryEntry>,
    onSelectDay: (Int) -> Unit,
    onDeleteMemory: (MemoryEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var searchQuery by remember { mutableStateOf("") }
    var selectedMoodFilter by remember { mutableStateOf<String?>(null) }
    var selectedPhaseFilter by remember { mutableStateOf<String?>(null) }

    val filteredMemories = memories.filter { entry ->
        val matchesQuery = searchQuery.isBlank() ||
                entry.title.contains(searchQuery, ignoreCase = true) ||
                entry.content.contains(searchQuery, ignoreCase = true) ||
                entry.growth_phase.contains(searchQuery, ignoreCase = true)
        val matchesMood = selectedMoodFilter == null || entry.moodTag == selectedMoodFilter
        val matchesPhase = selectedPhaseFilter == null || entry.growth_phase.equals(selectedPhaseFilter, ignoreCase = true)
        matchesQuery && matchesMood && matchesPhase
    }

    val textPrimary = if (isDark) Color(0xFFE9ECF2) else Color(0xFF1E232B)
    val textSecondary = if (isDark) Color(0xFF8F98A8) else Color(0xFF6B665E)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text("Search memories, sketches, phases...", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("archive_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF52B788),
                unfocusedBorderColor = if (isDark) Color(0xFF2E3442) else Color(0xFFE0DBD0)
            ),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter chips: Growth Phases
        val phases = listOf("All Phases", "Cot", "Hearth", "Garden", "Hamlet", "City")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(phases) { phase ->
                val isSelected = (phase == "All Phases" && selectedPhaseFilter == null) ||
                        (selectedPhaseFilter?.equals(phase, ignoreCase = true) == true)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedPhaseFilter = if (phase == "All Phases") null else phase.lowercase()
                    },
                    label = {
                        Text(phase, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (isDark) Color(0xFF2C3E33) else Color(0xFFE2EFE7),
                        selectedLabelColor = Color(0xFF52B788)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Filter chips: Moods
        val moods = listOf("All Moods", "Solitude", "Bloom", "Kinship", "Creation", "Insight", "Gratitude")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(moods) { mood ->
                val isSelected = (mood == "All Moods" && selectedMoodFilter == null) || (selectedMoodFilter == mood)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedMoodFilter = if (mood == "All Moods") null else mood
                    },
                    label = {
                        Text(mood, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (isDark) Color(0xFF332D24) else Color(0xFFFBF0DC),
                        selectedLabelColor = Color(0xFFE76F51)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Count header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SKETCHBOOK ARCHIVE (${filteredMemories.size} ENTRIES)",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredMemories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No Memories Found in This Folio",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Log a memory on any day to record your transition from cot to city.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = textSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredMemories, key = { it.id }) { memory ->
                    MemoryItemCard(
                        memory = memory,
                        onSelectDay = { onSelectDay(memory.dayNumber) },
                        onDelete = { onDeleteMemory(memory) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MemoryItemCard(
    memory: MemoryEntry,
    onSelectDay: () -> Unit,
    onDelete: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF1E232B) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF2C3240) else Color(0xFFE2DDD2)
    val textPrimary = if (isDark) Color(0xFFE9ECF2) else Color(0xFF1E232B)
    val textSecondary = if (isDark) Color(0xFF8F98A8) else Color(0xFF6B665E)

    val pigmentColor = try {
        Color(android.graphics.Color.parseColor(memory.paletteColorHex))
    } catch (e: Exception) {
        Color(0xFF52B788)
    }

    val dateFormatter = remember { SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()) }
    val formattedDate = dateFormatter.format(Date(memory.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, cardBorder, RoundedCornerShape(10.dp))
            .clickable { onSelectDay() }
            .testTag("memory_card_${memory.dayNumber}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Day stamp & Pigment dot & Phase badge
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
                            .background(pigmentColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DAY ${memory.dayNumber.toString().padStart(3, '0')} // $formattedDate",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = pigmentColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Growth Phase Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF52B788).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = memory.growth_phase.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF52B788),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isDark) Color(0xFF29313E) else Color(0xFFEDE9E0)
                    ) {
                        Text(
                            text = memory.moodTag,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp).testTag("delete_memory_${memory.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = memory.title,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = memory.content,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = textSecondary
            )

            // Optional attached image preview
            if (!memory.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFF52B788).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                ) {
                    AsyncImage(
                        model = memory.imageUrl,
                        contentDescription = "Memory photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Photo,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PHOTO",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            if (memory.promptQuestion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reflected on: \"${memory.promptQuestion}\"",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = if (isDark) Color(0xFFF4A261) else Color(0xFFE76F51),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}
