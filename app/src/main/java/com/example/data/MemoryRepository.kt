package com.example.data

import com.example.model.MemoryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MemoryRepository(private val memoryDao: MemoryDao) {
    val allMemories: Flow<List<MemoryEntry>> = memoryDao.getAllMemories()
    val recentMemories: Flow<List<MemoryEntry>> = memoryDao.getRecentMemories()
    val memoryCount: Flow<Int> = memoryDao.getMemoryCount()

    fun getMemoryForDay(dayNumber: Int): Flow<MemoryEntry?> {
        return memoryDao.getMemoryForDay(dayNumber)
    }

    suspend fun saveMemory(memory: MemoryEntry): Long {
        return memoryDao.insertMemory(memory)
    }

    suspend fun deleteMemory(memory: MemoryEntry) {
        memoryDao.deleteMemory(memory)
    }

    suspend fun initializeSeedDataIfEmpty() {
        val existing = memoryDao.getAllMemories().first()
        if (existing.isEmpty()) {
            val seedEntries = listOf(
                MemoryEntry(
                    dayNumber = 1,
                    date = System.currentTimeMillis() - 90L * 86400000L,
                    content = "A single wooden cot placed in the center of an empty white sketchbook. The silence is not emptiness, but an invitation to begin. 365 mornings wait ahead.",
                    growth_phase = "cot",
                    title = "The Blank Sheet & Solitary Cot",
                    moodTag = "Solitude",
                    paletteColorHex = "#7A8288",
                    tags = "beginning, cot, silence, day1",
                    promptQuestion = "What simple truth did you wake up with?"
                ),
                MemoryEntry(
                    dayNumber = 35,
                    date = System.currentTimeMillis() - 55L * 86400000L,
                    content = "Laid four corner posts of cedar around the cot. The night wind is softer now. Struck a flint against river rock to build the hearth fire.",
                    growth_phase = "hearth",
                    title = "First Hearth Stone & Timber Post",
                    moodTag = "Creation",
                    paletteColorHex = "#E76F51",
                    tags = "shelter, timber, warmth, fire",
                    promptQuestion = "What small shelter did you build today?"
                ),
                MemoryEntry(
                    dayNumber = 90,
                    date = System.currentTimeMillis() - 1L * 86400000L,
                    content = "Month 3 has arrived. Tomatoes, rosemary, and sweet pea vines climb the split rail fence. Dug a stone well where fresh groundwater mirrors the morning sun.",
                    growth_phase = "garden",
                    title = "The Garden Unfurls",
                    moodTag = "Bloom",
                    paletteColorHex = "#52B788",
                    tags = "garden, well, greenery, month3",
                    promptQuestion = "What seed sprouted that you had almost forgotten?"
                )
            )
            seedEntries.forEach { memoryDao.insertMemory(it) }
        }
    }
}
