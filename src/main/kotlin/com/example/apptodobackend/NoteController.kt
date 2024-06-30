package com.example.apptodobackend

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["http://localhost:5178"])
@RestController
@RequestMapping("/api/notes")
class NoteController {

    @Autowired
    lateinit var noteService: NoteService

    @GetMapping
    fun getAllNotes(): List<Note> {
        return noteService.getAllNotes()
    }

    @GetMapping("/{id}")
    fun getNoteById(@PathVariable id: Long): ResponseEntity<Note> {
        return noteService.getNoteById(id)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createNote(@RequestBody note: Note): Note {
        return noteService.saveNote(note)
    }

    @PutMapping("/{id}")
    fun updateNote(@PathVariable id: Long, @RequestBody updatedNote: Note): ResponseEntity<Note> {
        return noteService.updateNote(id, updatedNote)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteNoteById(@PathVariable id: Long): ResponseEntity<Void> {
        return if (noteService.deleteNoteById(id)) ResponseEntity.noContent().build() else ResponseEntity.notFound().build()
    }


    @PutMapping("/{id}/toggle-completion")
    fun toggleNoteCompletion(@PathVariable id: Long): ResponseEntity<Note> {
        return noteService.toggleNoteCompletion(id)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

}
