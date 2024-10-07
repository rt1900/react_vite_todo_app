package com.example.apptodobackend  //integration test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(properties = ["spring.config.name=application-test"])
@Transactional
@Rollback
class UserAccessIntegrationTest {

    @Autowired
    private lateinit var noteRepository: NoteRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `admin should have access to all notes`() {
        // Creating users and several notes
        val user1 = User(username = "user1", email = "user1@example.com", password = "password123", role = "USER")
        val user2 = User(username = "user2", email = "user2@example.com", password = "password456", role = "USER")
        userRepository.save(user1)
        userRepository.save(user2)

        val note1 = Note(title = "User1's Note", text = "User1's content", user = user1)
        val note2 = Note(title = "User2's Note", text = "User2's content", user = user2)
        noteRepository.save(note1)
        noteRepository.save(note2)

        // Admin should have access to all notes
        val allNotes = noteRepository.findAll()
        assertEquals(2, allNotes.size)
        assertTrue(allNotes.any { it.user == user1 })
        assertTrue(allNotes.any { it.user == user2 })
    }

    @Test
    @WithMockUser(username = "user1", roles = ["USER"])
    fun `user should only have access to their own notes`() {
        // Creating users and several notes
        val user1 = User(username = "user1", email = "user1@example.com", password = "password123", role = "USER")
        val user2 = User(username = "user2", email = "user2@example.com", password = "password456", role = "USER")
        userRepository.save(user1)
        userRepository.save(user2)

        val note1 = Note(title = "User1's Note", text = "User1's content", user = user1)
        val note2 = Note(title = "User2's Note", text = "User2's content", user = user2)
        noteRepository.save(note1)
        noteRepository.save(note2)

        // User should only have access to their own notes
        val user1Notes = noteRepository.findByUser(user1)
        assertEquals(1, user1Notes.size)
        assertEquals("User1's Note", user1Notes[0].title)

        // Verifying that the user cannot see notes from another user
        val user2Notes = noteRepository.findByUser(user2)
        assertTrue(user2Notes.none { it.user == user1 })
    }

    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `admin can delete and modify notes of other users`() {
        // Creating users and notes
        val user1 = User(username = "user1@example.com", email = "user1@example.com", password = "password123", role = "USER")
        val user2 = User(username = "user2@example.com", email = "user2@example.com", password = "password456", role = "USER")
        userRepository.save(user1)
        userRepository.save(user2)

        val note1 = Note(title = "User1's Note", text = "User1's content", user = user1)
        val note2 = Note(title = "User2's Note", text = "User2's content", user = user2)
        noteRepository.save(note1)
        noteRepository.save(note2)

        // Verifying that the notes were created
        assertEquals(2, noteRepository.count())

        // Admin edits the note of user 1
        note1.title = "Updated User1's Note"
        noteRepository.save(note1)

        // Verifying that the note was updated
        val updatedNote = noteRepository.findById(note1.id)
        assertTrue(updatedNote.isPresent)
        assertEquals("Updated User1's Note", updatedNote.get().title)

        // Admin deletes the note of user 2
        noteRepository.delete(note2)

        // Verifying that the note of user 2 was deleted
        assertEquals(1, noteRepository.count())
    }



}
