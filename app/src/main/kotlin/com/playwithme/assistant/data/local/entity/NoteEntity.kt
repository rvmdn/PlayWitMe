package com.playwithme.assistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.playwithme.assistant.domain.model.NoteColor

/** Room entity for floating Notes. */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val content: String,
    val title: String,
    val color: NoteColor,  // Converted by RoomConverters
    val isPinned: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
