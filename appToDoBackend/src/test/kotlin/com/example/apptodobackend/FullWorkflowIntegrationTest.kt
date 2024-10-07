package com.example.apptodobackend   //integration test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.test.annotation.Rollback
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback
@ActiveProfiles("test")
class FullWorkflowIntegrationTest {

    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `test full user workflow`() {
        // Step 1: Register a new user
        val registerUrl = "http://localhost:$port/api/register"
        val registerBody = """
            {
                "username": "test100@example.com",
                "email": "test100@example.com",
                "password": "password123"
            }
        """
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val registerRequest = HttpEntity(registerBody, headers)
        val registerResponse = restTemplate.postForEntity(registerUrl, registerRequest, String::class.java)
        assertEquals(HttpStatus.OK, registerResponse.statusCode)

        // Step 2: Login the user and get JWT token
        val loginUrl = "http://localhost:$port/api/login"
        val loginBody = """
            {
                "email": "test100@example.com",
                "password": "password123"
            }
        """
        val loginRequest = HttpEntity(loginBody, headers)
        val loginResponse = restTemplate.postForEntity(loginUrl, loginRequest, String::class.java)
        assertEquals(HttpStatus.OK, loginResponse.statusCode)

        // Extract the Authorization token from the login response
        val authToken = loginResponse.body?.let { parseAuthToken(it) }
        println("Auth token: $authToken")  // Output the token to the console for verification


        assertNotNull(authToken, "Authorization token should not be null")

        //Create a new note (using Authorization token)
        val createNoteUrl = "http://localhost:$port/api/notes"
        val noteBody = """
            {
                "title": "My first note",
                "text": "This is a test note"
            }
        """
        headers.set("Authorization", "Bearer $authToken")  // Adding the token to the header
        val createNoteRequest = HttpEntity(noteBody, headers)
        val createNoteResponse = restTemplate.postForEntity(createNoteUrl, createNoteRequest, String::class.java)
        assertEquals(HttpStatus.OK, createNoteResponse.statusCode)

        // Get the created note
        val getNotesUrl = "http://localhost:$port/api/notes"
        val getNotesResponse = restTemplate.exchange(getNotesUrl, HttpMethod.GET, HttpEntity(null, headers), String::class.java)
        assertEquals(HttpStatus.OK, getNotesResponse.statusCode)

        // Edit the note (Use PUT instead of POST)
        val editNoteUrl = "http://localhost:$port/api/notes/1"
        val editNoteBody = """
            {
                "title": "Updated note",
                "text": "This is an updated test note"
            }
        """
        val editNoteRequest = HttpEntity(editNoteBody, headers)
        val editNoteResponse = restTemplate.exchange(editNoteUrl, HttpMethod.PUT, editNoteRequest, String::class.java)  // Using PUT
        assertEquals(HttpStatus.OK, editNoteResponse.statusCode)

        // Delete the note
        val deleteNoteUrl = "http://localhost:$port/api/notes/1"
        val deleteNoteResponse = restTemplate.exchange(deleteNoteUrl, HttpMethod.DELETE, HttpEntity(null, headers), String::class.java)

        // Changing the status check from 200 to 204
        assertEquals(HttpStatus.NO_CONTENT, deleteNoteResponse.statusCode)


        //  Logout the user
        val logoutUrl = "http://localhost:$port/api/logout"
        val logoutResponse = restTemplate.postForEntity(logoutUrl, null, String::class.java)
        assertEquals(HttpStatus.OK, logoutResponse.statusCode)
    }

    private fun parseAuthToken(responseBody: String): String? {
        val jsonNode = ObjectMapper().readTree(responseBody)
        return jsonNode.get("token")?.asText()
    }
}
