package com.example.apptodobackend

import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class NoteService(private val noteRepository: NoteRepository) {

    /*fun getAllNotes(): List<Note> {
        return noteRepository.findAll()
    }*/

    fun getAllNotes(): List<Note> {
        return noteRepository.findAll().sortedByDescending { it.lastUpdated } // сортируем по последнему обновлению
    }

    fun getNoteById(id: Long): Note? {
        return noteRepository.findById(id).orElse(null)
    }

   /* fun saveNote(note: Note): Note {
        return noteRepository.save(note)
    }*/

    fun saveNote(note: Note): Note {
        note.lastUpdated = LocalDateTime.now() // устанавливаем время создания
        return noteRepository.save(note)
    }

    //это черновик:
    /*fun updateNote(id: Long, updatedNote: Note): Note? {
        return if (noteRepository.existsById(id)) {
            noteRepository.save(updatedNote.copy(id = id))
        } else {
            null
        }
    }*/


    fun updateNote(id: Long, updatedNote: Note): Note? {
        return if (noteRepository.existsById(id)) {
            val existingNote = noteRepository.findById(id).orElse(null)
            if (existingNote != null) {
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

}
