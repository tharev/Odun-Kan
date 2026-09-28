package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.MemoryEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY dayNumber ASC")
    fun getAllMemories(): Flow<List<MemoryEntry>>

    @Query("SELECT * FROM memories WHERE dayNumber = :dayNumber LIMIT 1")
    fun getMemoryForDay(dayNumber: Int): Flow<MemoryEntry?>

    @Query("SELECT * FROM memories WHERE dayNumber = :dayNumber LIMIT 1")
    suspend fun getMemoryForDayDirect(dayNumber: Int): MemoryEntry?

    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    fun getRecentMemories(): Flow<List<MemoryEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntry): Long

    @Update
    suspend fun updateMemory(memory: MemoryEntry)

    @Delete
    suspend fun deleteMemory(memory: MemoryEntry)

    @Query("SELECT COUNT(*) FROM memories")
    fun getMemoryCount(): Flow<Int>

    @Query("SELECT MAX(dayNumber) FROM memories")
    fun getMaxLoggedDay(): Flow<Int?>
}
