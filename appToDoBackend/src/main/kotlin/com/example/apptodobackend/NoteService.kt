package com.example.apptodobackend

import org.springframework.stereotype.Service
import java.time.LocalDateTime




@Service
class NoteService(private val noteRepository: NoteRepository, private val userRepository: UserRepository) {



    fun getAllNotes(): List<Note> {
        return noteRepository.findAll().sortedByDescending { it.lastUpdated } // сортируем по последнему обновлению
    }

    fun getNoteById(id: Long): Note? {
        return noteRepository.findById(id).orElse(null)
    }


    fun saveNote(note: Note): Note {
        note.createdAt = LocalDateTime.now()
        note.lastUpdated = LocalDateTime.now()
        return noteRepository.save(note)
    }



    fun updateNote(id: Long, updatedNote: Note): Note? {
        return if (noteRepository.existsById(id)) {
            val existingNote = noteRepository.findById(id).orElse(null)
            if (existingNote != null) {
                existingNote.title = updatedNote.title
                existingNote.text = updatedNote.text
                existingNote.isCompleted = updatedNote.isCompleted
                existingNote.lastUpdated = LocalDateTime.now() // обновляем поле
                noteRepository.save(existingNote)
            } else {
                null
            }
        } else {
            null
        }
    }

    fun deleteNoteById(id: Long): Boolean {
        return if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id)
            true
        } else {
            false
        }
    }


    fun toggleNoteCompletion(id: Long): Note? {
        val note = noteRepository.findById(id).orElse(null)
        return if (note != null) {
            note.isCompleted = !note.isCompleted
            note.lastUpdated = LocalDateTime.now()
            noteRepository.save(note)
        } else {
            null
        }
    }

    fun findUserByUsername(username: String): User? {
        return userRepository.findByEmail(username)
    }

    fun getNotesByUser(user: User): List<Note> {
        return noteRepository.findByUser(user)
    }

    fun getAllNotesForCurrentUser(username: String): List<Note> {
        val user = userRepository.findByEmail(username)
        return if (user != null) {
            getNotesByUser(user)
        } else {
            emptyList()
        }
    }



}
