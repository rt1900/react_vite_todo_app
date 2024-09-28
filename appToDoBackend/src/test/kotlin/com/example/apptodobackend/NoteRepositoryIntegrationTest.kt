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

        // Сохраняем заметку в базе данных
        val savedNote = noteRepository.save(note)

        // Проверяем, что заметка успешно сохранилась и вернулась с корректными данными
        assertNotNull(savedNote.id)
        assertEquals("Test Note", savedNote.title)
        assertEquals("This is a test note", savedNote.text)

        // Извлекаем заметку из базы данных по ее ID
        val foundNote = noteRepository.findById(savedNote.id!!).orElse(null)

        // Проверяем, что извлеченная заметка соответствует сохраненной
        assertNotNull(foundNote)
        assertEquals("Test Note", foundNote?.title)
        assertEquals("This is a test note", foundNote?.text)
    }

    @Test
    fun `test findByUser should return notes for a specific user`() {
        // Создаем пользователя и сохраняем его
        val user = User(username = "testUser", email = "test@example.com", password = "password123", role = "USER")
        userRepository.save(user)

        // Создаем несколько заметок для пользователя
        val note1 = Note(title = "Note 1", text = "Text 1", user = user)
        val note2 = Note(title = "Note 2", text = "Text 2", user = user)
        noteRepository.save(note1)
        noteRepository.save(note2)

        // Выполняем метод findByUser
        val notes = noteRepository.findByUser(user)

        // Проверяем, что возвращены правильные заметки
        assertEquals(2, notes.size)
        assertTrue(notes.any { it.title == "Note 1" })
        assertTrue(notes.any { it.title == "Note 2" })
    }
}
