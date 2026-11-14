package com.playwithme.assistant.domain.usecase

import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.domain.model.Note
import com.playwithme.assistant.domain.model.NoteColor
import com.playwithme.assistant.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

/** Use case: Get all notes as a live Flow. */
class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> = repository.getAllNotes()
}

/** Use case: Get pinned notes. */
class GetPinnedNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> = repository.getPinnedNotes()
}

/** Use case: Save a new note. */
class SaveNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    /**
     * Creates and saves a new note.
     *
     * @param content Note content (required, non-blank)
     * @param title Optional note title
     * @param color Optional note color
     */
    suspend operator fun invoke(
        content: String,
        title: String = "",
        color: NoteColor = NoteColor.DEFAULT
    ): Result<Note> {
        if (content.isBlank()) {
            return Result.Error(IllegalArgumentException("Note content cannot be blank"))
        }
        val now = System.currentTimeMillis()
        val note = Note(
            id = UUID.randomUUID().toString(),
            content = content.trim(),
            title = title.trim(),
            color = color,
            createdAt = now,
            updatedAt = now
        )
        return repository.saveNote(note)
    }
}

/** Use case: Update an existing note. */
class UpdateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note): Result<Unit> =
        repository.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
}

/** Use case: Delete a note by ID. */
class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(noteId: String): Result<Unit> =
        repository.deleteNote(noteId)
}
