package com.playwithme.assistant.domain.repository

import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.domain.model.Note
import kotlinx.coroutines.flow.Flow

/** Repository interface for floating notes. */
interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getPinnedNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: String): Result<Note>
    suspend fun saveNote(note: Note): Result<Note>
    suspend fun updateNote(note: Note): Result<Unit>
    suspend fun deleteNote(id: String): Result<Unit>
    suspend fun deleteAllNotes(): Result<Unit>
}
