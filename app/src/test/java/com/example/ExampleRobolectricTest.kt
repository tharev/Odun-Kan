package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.MemoryDao
import com.example.data.MemoryRepository
import com.example.di.databaseModule
import com.example.model.MemoryEntry
import com.example.ui.isometric.WorldStage
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ọdún kan", appName)
    }

    @Test
    fun `verify world stages progression`() {
        assertEquals(WorldStage.THE_COT, WorldStage.fromDay(1))
        assertEquals(WorldStage.THE_COT, WorldStage.fromDay(30))
        assertEquals(WorldStage.THE_HEARTH, WorldStage.fromDay(45))
        assertEquals(WorldStage.THE_GARDEN, WorldStage.fromDay(95))
        assertEquals(WorldStage.THE_HAMLET, WorldStage.fromDay(200))
        assertEquals(WorldStage.THE_CITY, WorldStage.fromDay(365))
    }

    @Test
    fun `verify memory entry entity fields and growth phase transition`() {
        val now = System.currentTimeMillis()
        val entry = MemoryEntry(
            id = 1L,
            dayNumber = 120,
            date = now,
            content = "Lush green garden beds flourishing by the stone well.",
            growth_phase = MemoryEntry.growthPhaseForDay(120),
            title = "Garden Harvest"
        )

        assertEquals(now, entry.date)
        assertEquals("Lush green garden beds flourishing by the stone well.", entry.content)
        assertEquals("garden", entry.growth_phase)

        // Verify transition mapping from cot to city
        assertEquals("cot", MemoryEntry.growthPhaseForDay(1))
        assertEquals("cot", MemoryEntry.growthPhaseForDay(30))
        assertEquals("hearth", MemoryEntry.growthPhaseForDay(31))
        assertEquals("garden", MemoryEntry.growthPhaseForDay(91))
        assertEquals("hamlet", MemoryEntry.growthPhaseForDay(181))
        assertEquals("city", MemoryEntry.growthPhaseForDay(271))
        assertEquals("city", MemoryEntry.growthPhaseForDay(365))
    }

    @Test
    fun `verify database module provides dependencies`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        stopKoin()
        startKoin {
            androidContext(context)
            modules(databaseModule)
        }

        val koin = GlobalContext.get()
        val database = koin.get<AppDatabase>()
        val dao = koin.get<MemoryDao>()
        val repository = koin.get<MemoryRepository>()

        assertNotNull(database)
        assertNotNull(dao)
        assertNotNull(repository)
    }
}
