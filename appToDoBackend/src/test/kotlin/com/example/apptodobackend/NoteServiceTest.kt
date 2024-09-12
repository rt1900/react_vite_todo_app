package com.example.apptodobackend

import com.example.apptodobackend.Note
import com.example.apptodobackend.NoteRepository
import com.example.apptodobackend.NoteService
import com.example.apptodobackend.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.Optional

class NoteServiceTest {

    @Test
    fun `test saveNote should save note successfully`() {
        // Мокаем зависимости
        val noteRepository = mock(NoteRepository::class.java)
        val userRepository = mock(UserRepository::class.java)

        // Создаем объект NoteService с мокнутыми зависимостями
        val noteService = NoteService(noteRepository, userRepository)

        // Создаем тестовую заметку
        val note = Note(title = "Test Note", text = "This is a test note")

        // Задаем поведение мокнутого noteRepository
        `when`(noteRepository.save(note)).thenReturn(note)

        // Вызываем тестируемый метод
        val savedNote = noteService.saveNote(note)

        // Проверяем результат
        assertEquals("Test Note", savedNote.title)
        assertEquals("This is a test note", savedNote.text)

        // Проверяем, что метод save был вызван
        verify(noteRepository).save(note)
    }



    @Test
    fun `test updateNote should update note successfully`() {
        // Мокаем зависимости
        val noteRepository = mock(NoteRepository::class.java)
        val noteService = NoteService(noteRepository, mock(UserRepository::class.java))

        // Создаем тестовую заметку
        val existingNote = Note(id = 1L, title = "Old Title", text = "Old Text")
        val updatedNote = Note(id = 1L, title = "New Title", text = "New Text")

        // Мокаем, что заметка существует в репозитории
        `when`(noteRepository.existsById(existingNote.id)).thenReturn(true)
        `when`(noteRepository.findById(existingNote.id)).thenReturn(Optional.of(existingNote))

        // Мокаем сохранение обновленной заметки
        `when`(noteRepository.save(existingNote)).thenReturn(updatedNote)

        // Вызываем метод
        val result = noteService.updateNote(existingNote.id, updatedNote)

        // Проверяем результат
        assertNotNull(result)
        assertEquals("New Title", result?.title)
        assertEquals("New Text", result?.text)
    }




    @Test
    fun `test deleteNoteById should delete note successfully`() {
        // Мокаем зависимости
        val noteRepository = mock(NoteRepository::class.java)
        val noteService = NoteService(noteRepository, mock(UserRepository::class.java))

        // Мокаем, что заметка существует
        `when`(noteRepository.existsById(1L)).thenReturn(true)

        // Вызываем метод
        val isDeleted = noteService.deleteNoteById(1L)

        // Проверяем результат
        assertTrue(isDeleted)
        verify(noteRepository).deleteById(1L)
    }


    @Test
    fun `test toggleNoteCompletion should toggle completion status`() {
        // Мокаем зависимости
        val noteRepository = mock(NoteRepository::class.java)
        val noteService = NoteService(noteRepository, mock(UserRepository::class.java))

        // Создаем тестовую заметку с isCompleted = false
        val note = Note(id = 1L, title = "Test Note", text = "Test text", isCompleted = false)

        // Мокаем, что заметка существует в репозитории
        `when`(noteRepository.findById(1L)).thenReturn(Optional.of(note))

        // Мокаем сохранение обновлённой заметки
        `when`(noteRepository.save(note)).thenReturn(note.copy(isCompleted = true))

        // Вызываем метод
        val updatedNote = noteService.toggleNoteCompletion(1L)

        // Проверяем, что состояние изменилось
        assertNotNull(updatedNote) // проверяем, что updatedNote не null
        assertTrue(updatedNote?.isCompleted == true) // проверяем, что isCompleted стал true

        // Проверяем, что метод save был вызван
        verify(noteRepository).save(note)
    }



    @Test
    fun `test getNoteById should return note if exists`() {
        // Мокаем зависимости
        val noteRepository = mock(NoteRepository::class.java)
        val noteService = NoteService(noteRepository, mock(UserRepository::class.java))

        // Создаем тестовую заметку
        val note = Note(id = 1L, title = "Test Note", text = "Test text")

        // Мокаем, что заметка существует в репозитории
        `when`(noteRepository.findById(1L)).thenReturn(Optional.of(note))

        // Вызываем метод
        val result = noteService.getNoteById(1L)

        // Проверяем результат
        assertNotNull(result)
        assertEquals("Test Note", result?.title)
        assertEquals("Test text", result?.text)
    }












}



