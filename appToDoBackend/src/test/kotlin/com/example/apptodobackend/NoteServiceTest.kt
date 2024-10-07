package com.example.apptodobackend    //unitTest


import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.Optional

class NoteServiceTest {

    @Test
    fun `test saveNote should save note successfully`() {
        // Mocking dependencies
        val noteRepository = mock(NoteRepository::class.java)
        val userRepository = mock(UserRepository::class.java)

        // Creating a NoteService object with mocked dependencies
        val noteService = NoteService(noteRepository, userRepository)

        // Creating a test note
        val note = Note(title = "Test Note", text = "This is a test note")

        // Defining the behavior of the mocked noteRepository
        `when`(noteRepository.save(note)).thenReturn(note)

        // Calling the method under test
        val savedNote = noteService.saveNote(note)

        // Verifying the result
        assertEquals("Test Note", savedNote.title)
        assertEquals("This is a test note", savedNote.text)

        // Verifying that the save method was called
        verify(noteRepository).save(note)
    }



    @Test
    fun `test updateNote should update note successfully`() {
        // Mocking dependencies
        val noteRepository = mock(NoteRepository::class.java)
        val noteService = NoteService(noteRepository, mock(UserRepository::class.java))

        // Creating a test note
        val existingNote = Note(id = 1L, title = "Old Title", text = "Old Text")
        val updatedNote = Note(id = 1L, title = "New Title", text = "New Text")

        // Mocking that the note exists in the repository
        `when`(noteRepository.existsById(existingNote.id)).thenReturn(true)
        `when`(noteRepository.findById(existingNote.id)).thenReturn(Optional.of(existingNote))

        // Mocking the saving of the updated note
        `when`(noteRepository.save(existingNote)).thenReturn(updatedNote)

        // Calling the method
        val result = noteService.updateNote(existingNote.id, updatedNote)

        // Verifying the result
        assertNotNull(result)
        assertEquals("New Title", result?.title)
        assertEquals("New Text", result?.text)
    }




    @Test
    fun `test deleteNoteById should delete note successfully`() {
        // Mocking dependencies
        val noteRepository = mock(NoteRepository::class.java)
        val noteService = NoteService(noteRepository, mock(UserRepository::class.java))

        // Mocking that the note exists
        `when`(noteRepository.existsById(1L)).thenReturn(true)

        // Calling the method
        val isDeleted = noteService.deleteNoteById(1L)

        // Verifying the result
        assertTrue(isDeleted)
        verify(noteRepository).deleteById(1L)
    }


    @Test
    fun `test toggleNoteCompletion should toggle completion status`() {
        // Mocking dependencies
        val noteRepository = mock(NoteRepository::class.java)
        val noteService = NoteService(noteRepository, mock(UserRepository::class.java))

        // Creating a test note with isCompleted = false
        val note = Note(id = 1L, title = "Test Note", text = "Test text", isCompleted = false)

        // Mocking that the note exists in the repository
        `when`(noteRepository.findById(1L)).thenReturn(Optional.of(note))

        // Mocking the saving of the updated note
        `when`(noteRepository.save(note)).thenReturn(note.copy(isCompleted = true))

        // Calling the method
        val updatedNote = noteService.toggleNoteCompletion(1L)

        // Verifying that the status has changed
        assertNotNull(updatedNote) // verifying that updatedNote is not null
        assertTrue(updatedNote?.isCompleted == true) // verifying that isCompleted is now true

        // Verifying that the save method was called
        verify(noteRepository).save(note)
    }



    @Test
    fun `test getNoteById should return note if exists`() {
        // Mocking dependencies
        val noteRepository = mock(NoteRepository::class.java)
        val noteService = NoteService(noteRepository, mock(UserRepository::class.java))

        // Creating a test note
        val note = Note(id = 1L, title = "Test Note", text = "Test text")

        // Mocking that the note exists in the repository
        `when`(noteRepository.findById(1L)).thenReturn(Optional.of(note))

        // Calling the method
        val result = noteService.getNoteById(1L)

        // Verifying the result
        assertNotNull(result)
        assertEquals("Test Note", result?.title)
        assertEquals("Test text", result?.text)
    }

    // Creating mock for noteRepository and noteService
    private val noteRepository = mock(NoteRepository::class.java)
    private val noteService = NoteService(noteRepository, mock(UserRepository::class.java))

    @Test
    fun `getNoteById should throw exception if note does not exist`() {
        // Mocking the repository to return an empty result (note not found)
        `when`(noteRepository.findById(1L)).thenReturn(Optional.empty())

        // Verifying that the method throws NoteNotFoundException if the note is not found
        assertThrows(NoteNotFoundException::class.java) {
            noteService.getNoteById(1L)
        }
    }




}



