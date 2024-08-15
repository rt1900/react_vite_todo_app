package com.example.apptodobackend

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.access.prepost.PreAuthorize


@CrossOrigin(origins = ["http://localhost:5178"])
@RestController
@RequestMapping("/api/notes")
class NoteController {

    @Autowired
    lateinit var noteService: NoteService

    @GetMapping
    fun getAllNotes(authentication: Authentication): List<Note> {
        val currentUser = authentication.principal as UserDetails
        return noteService.getAllNotesForCurrentUser(currentUser.username)
    }


    @GetMapping("/{id}")
    fun getNoteById(@PathVariable id: Long): ResponseEntity<Note> {
        return noteService.getNoteById(id)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    fun createNote(@RequestBody note: Note, authentication: Authentication): Note {
        println("Request to create note received")
        println("Received request to create note from user: ${authentication.principal}")

        val currentUser = authentication.principal as UserDetails // Получаю данные о текущем пользователе
        println("User authenticated: ${currentUser.username}, Authorities: ${currentUser.authorities}")
        val appUser = noteService.findUserByUsername(currentUser.username) // Нахожу пользователя в базе данных

        note.user = appUser
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

    @GetMapping("/test")
    fun testEndpoint(): ResponseEntity<String> {
        println("Test endpoint hit")
        return ResponseEntity.ok("Test successful")
    }


}
