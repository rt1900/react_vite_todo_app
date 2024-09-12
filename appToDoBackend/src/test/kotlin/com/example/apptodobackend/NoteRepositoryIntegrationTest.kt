package com.example.apptodobackend

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class NoteRepositoryIntegrationTest {

    @Autowired
    lateinit var noteRepository: NoteRepository

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
}
