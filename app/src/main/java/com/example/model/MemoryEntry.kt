package com.example.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayNumber: Int = 1, // 1 to 365
    val date: Long = System.currentTimeMillis(), // date field
    val content: String = "", // content field
    @ColumnInfo(name = "growth_phase")
    val growth_phase: String = "cot", // 'growth_phase' indicator tracking transition from cot to city
    val title: String = "",
    val moodTag: String = "Bloom", // Solitude, Bloom, Kinship, Creation, Insight, Gratitude
    val paletteColorHex: String = "#52B788",
    val tags: String = "",
    val promptQuestion: String = "",
    val bloomIntensity: Float = 1.0f,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun growthPhaseForDay(day: Int): String {
            return when {
                day <= 30 -> "cot"
                day <= 90 -> "hearth"
                day <= 180 -> "garden"
                day <= 270 -> "hamlet"
                else -> "city"
            }
        }
    }
}

data class Milestone(
    val stage: Int,
    val name: String,
    val yorubaSubtitle: String,
    val dayRange: IntRange,
    val description: String,
    val keyElement: String,
    val unlocked: Boolean
)
