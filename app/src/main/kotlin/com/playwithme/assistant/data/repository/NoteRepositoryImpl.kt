package com.playwithme.assistant.data.repository

import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.core.common.safeCall
import com.playwithme.assistant.data.local.dao.NoteDao
import com.playwithme.assistant.data.local.mapper.toDomain
import com.playwithme.assistant.data.local.mapper.toEntity
import com.playwithme.assistant.domain.model.Note
import com.playwithme.assistant.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAllNotes().map { entities -> entities.map { it.toDomain() } }

    override fun getPinnedNotes(): Flow<List<Note>> =
        noteDao.getPinnedNotes().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getNoteById(id: String): Result<Note> = safeCall {
        noteDao.getNoteById(id)?.toDomain()
            ?: throw NoSuchElementException("Note not found: $id")
    }

    override suspend fun saveNote(note: Note): Result<Note> = safeCall {
        noteDao.insertNote(note.toEntity())
        note
    }

    override suspend fun updateNote(note: Note): Result<Unit> = safeCall {
        noteDao.updateNote(note.toEntity())
    }

    override suspend fun deleteNote(id: String): Result<Unit> = safeCall {
        noteDao.deleteNoteById(id)
    }

    override suspend fun deleteAllNotes(): Result<Unit> = safeCall {
        noteDao.deleteAllNotes()
    }
}
