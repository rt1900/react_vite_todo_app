package com.example.apptodobackend      //integration test

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.http.MediaType


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("postgresql")
class NoteControllerTest {

    @Autowired
    private lateinit var customUserDetailsService: CustomUserDetailsService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var noteRepository: NoteRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        // Database cleanup before each test
        noteRepository.deleteAll()
        userRepository.deleteAll()

        //  Creating a user for tests
        val user = User(username = "test@example.com", email = "test@example.com", password = "password", role = "ROLE_USER")
        userRepository.save(user)


        // Creating notes for the user
        val note1 = Note(title = "Note 1", text = "Text 1", user = user)
        val note2 = Note(title = "Note 2", text = "Text 2", user = user)
        noteRepository.save(note1)
        noteRepository.save(note2)
    }

    @Test
    fun `test getAllNotes with JWT should return status 200`() {

        val userDetails = customUserDetailsService.loadUserByUsername("test@example.com")

        // Creating an Authentication object based on UserDetails
        val authentication: Authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

        // Generating a token
        val token = jwtTokenProvider.generateToken(authentication)

        mockMvc.perform(get("/api/notes")
            .header("Authorization", "Bearer $token")) // Adding the token to the header
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2)) //Verifying that 2 notes were returned
    }

    @Test
    fun `test create note through API`() {
        // Creating a new user
        val user = User(username = "testUser", email = "test1@example.com", password = "password", role = "ROLE_USER")
        userRepository.save(user)

        // Logging in and obtaining a token
        val userDetails = customUserDetailsService.loadUserByUsername("test1@example.com")
        val authentication: Authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        val token = jwtTokenProvider.generateToken(authentication)

        // New note data
        val newNote = mapOf("title" to "New Note", "text" to "Note content")

        // Sending a request to create a new note
        mockMvc.perform(post("/api/notes")
            .header("Authorization", "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newNote)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("New Note"))
            .andExpect(jsonPath("$.text").value("Note content"))
    }

    @Test
    fun `test update note through API`() {
        // Creating a new user
        val user = User(username = "testUser", email = "test2@example.com", password = "password", role = "ROLE_USER")
        userRepository.save(user)

        // Logging in and obtaining a token
        val userDetails = customUserDetailsService.loadUserByUsername("test2@example.com")
        val authentication: Authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        val token = jwtTokenProvider.generateToken(authentication)

        // Creating a note
        val note = Note(title = "Original Title", text = "Original Content", user = user)
        noteRepository.save(note)

        // Data for update
        val updatedNote = mapOf("title" to "Updated Title", "text" to "Updated Content")

        // Sending a request to update the note
        mockMvc.perform(put("/api/notes/${note.id}")  // Path to the specific note
            .header("Authorization", "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedNote)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.text").value("Updated Content"))
    }


    @Test
    fun `test delete note through API`() {
        // Creating a new user
        val user = User(username = "testUser", email = "test3@example.com", password = "password", role = "ROLE_USER")
        userRepository.save(user)

        // Logging in and obtaining a token
        val userDetails = customUserDetailsService.loadUserByUsername("test3@example.com")
        val authentication: Authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        val token = jwtTokenProvider.generateToken(authentication)

        // Creating and saving a note
        val note = Note(title = "Title to be deleted", text = "Content to be deleted", user = user)
        val savedNote = noteRepository.save(note)  //Saving the note to the database and retrieving its ID

        // Ensure the note is saved
        mockMvc.perform(get("/api/notes/${savedNote.id}")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)

        // Send a request to delete the note
        mockMvc.perform(delete("/api/notes/${savedNote.id}")  //  Using the saved note's ID
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isNoContent)  // Expecting status 204 for successful deletion

        // Verify that the note has been deleted (attempt to retrieve it returns 404)
        mockMvc.perform(get("/api/notes/${savedNote.id}")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isNotFound)  // Expecting status 404 after deletion

    }

    @Test
    fun `test get note by ID through API`() {
        // Creating a new user
        val user = User(username = "testUser", email = "test4@example.com", password = "password", role = "ROLE_USER")
        userRepository.save(user)

        // Logging in and obtaining a token
        val userDetails = customUserDetailsService.loadUserByUsername("test4@example.com")
        val authentication: Authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        val token = jwtTokenProvider.generateToken(authentication)

        // Creating and saving a note
        val note = Note(title = "Test Note", text = "This is a test note.", user = user)
        val savedNote = noteRepository.save(note)  // Saving the note to the database and obtaining its ID

        // Performing a GET request to retrieve the note by its ID
        mockMvc.perform(get("/api/notes/${savedNote.id}")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)  // Expecting status 200 OK
            .andExpect(jsonPath("$.title").value("Test Note"))  // Verifying that the title matches
            .andExpect(jsonPath("$.text").value("This is a test note."))  // Verifying the note text
            .andExpect(jsonPath("$.user.email").value(user.email))  // Verifying that the user matches
    }


}

