package com.playwithme.assistant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.playwithme.assistant.data.local.dao.HeroDao
import com.playwithme.assistant.data.local.dao.NoteDao
import com.playwithme.assistant.data.local.entity.HeroEntity
import com.playwithme.assistant.data.local.entity.NoteEntity

/**
 * PlayWitMe Room Database.
 *
 * Single database instance managed by Hilt — never instantiate directly.
 * Schema exported to /schemas/ directory for migration validation.
 */
@Database(
    entities = [
        HeroEntity::class,
        NoteEntity::class,
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun heroDao(): HeroDao
    abstract fun noteDao(): NoteDao
}
