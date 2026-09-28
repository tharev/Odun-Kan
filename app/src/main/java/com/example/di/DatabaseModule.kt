package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.MemoryDao
import com.example.data.MemoryRepository
import org.koin.dsl.module

val databaseModule = module {
    // Provide Room AppDatabase instance for MemoryEntry
    single<AppDatabase> {
        Room.databaseBuilder(
            get<Context>(),
            AppDatabase::class.java,
            "odun_kan.db"
        ).fallbackToDestructiveMigration(true)
         .build()
    }

    // Provide MemoryDao instance from AppDatabase
    single<MemoryDao> {
        get<AppDatabase>().memoryDao()
    }

    // Provide MemoryRepository for dependency injection
    single<MemoryRepository> {
        MemoryRepository(get())
    }
}
