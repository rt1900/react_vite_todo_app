package com.example.apptodobackend

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.access.prepost.PreAuthorize
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority


@CrossOrigin(origins = ["http://localhost:5178"])
@RestController
@RequestMapping("/api/notes")
class NoteController {
    private val logger: Logger = LoggerFactory.getLogger(NoteController::class.java)

    @Autowired
    lateinit var noteService: NoteService

    @GetMapping
    fun getAllNotes(authentication: Authentication): ResponseEntity<List<Note>> {
        val currentUser = authentication.principal as UserDetails

        return if (currentUser.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN"))) {
            // Если это админ, возвращаем все заметки
            ResponseEntity.ok(noteService.getAllNotes())  // Этот метод должен возвращать все заметки
        } else {
            // Если это обычный пользователь, возвращаем только его заметки
            ResponseEntity.ok(noteService.getAllNotesForCurrentUser(currentUser.username))
        }
    }


    @GetMapping("/{id}")
    fun getNoteById(@PathVariable id: Long, authentication: Authentication): ResponseEntity<Note> {
        val note = noteService.getNoteById(id)
        logger.info("Loaded note: $note")
        logger.info("Note user email: ${note?.user?.email}, Authenticated user: ${authentication.name}")

        return if (note != null && note.user?.email == authentication.name) {
            logger.info("Access granted to user: ${authentication.name}")
            ResponseEntity.ok(note)
        } else {
            logger.info("Access denied for user: ${authentication.name}")
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    fun createNote(@RequestBody note: Note, authentication: Authentication): Note {

        println("Request to create note received")
        println("Received request to create note from user: ${authentication.principal}")

        logger.info("Received a request to create a note from the user: ${authentication.name}")
        val currentUser = authentication.principal as UserDetails // Получаю данные о текущем пользователе
        println("User authenticated: ${currentUser.username}, Authorities: ${currentUser.authorities}")
        val appUser = noteService.findUserByEmail(currentUser.username) // Используем email для поиска пользователя
        if (appUser != null) {
            logger.info("User found in the database ${appUser.email}")
            note.user = appUser
        } else {
            logger.error("User not found in the database")
            throw RuntimeException("User not found")
        }



        note.user = appUser
        logger.info("Saving the note is starting")
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
