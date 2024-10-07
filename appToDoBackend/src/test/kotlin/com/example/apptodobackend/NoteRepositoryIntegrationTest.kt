package com.example.apptodobackend  //integration test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(properties = ["spring.config.name=application-test"])
@Transactional
@Rollback
class NoteRepositoryIntegrationTest {

    @Autowired
    private lateinit var noteRepository: NoteRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `test save and retrieve note`() {
        // Создаем новую заметку
        val note = Note(
            title = "Test Note",
            text = "This is a test note",
            isCompleted = false
        )

        // Saving the note to the database
        val savedNote = noteRepository.save(note)

        // Verifying that the note was successfully saved and returned with correct data
        assertNotNull(savedNote.id)
        assertEquals("Test Note", savedNote.title)
        assertEquals("This is a test note", savedNote.text)

        // Retrieving the note from the database by its ID
        val foundNote = noteRepository.findById(savedNote.id!!).orElse(null)

        // Verifying that the retrieved note matches the saved one
        assertNotNull(foundNote)
        assertEquals("Test Note", foundNote?.title)
        assertEquals("This is a test note", foundNote?.text)
    }

    @Test
    fun `test findByUser should return notes for a specific user`() {
        // Creating and saving a user
        val user = User(username = "testUser", email = "test@example.com", password = "password123", role = "USER")
        userRepository.save(user)

        // Creating several notes for the user
        val note1 = Note(title = "Note 1", text = "Text 1", user = user)
        val note2 = Note(title = "Note 2", text = "Text 2", user = user)
        noteRepository.save(note1)
        noteRepository.save(note2)

        // Executing the findByUser method
        val notes = noteRepository.findByUser(user)

        // Verifying that the correct notes are returned
        assertEquals(2, notes.size)
        assertTrue(notes.any { it.title == "Note 1" })
        assertTrue(notes.any { it.title == "Note 2" })
    }
}
