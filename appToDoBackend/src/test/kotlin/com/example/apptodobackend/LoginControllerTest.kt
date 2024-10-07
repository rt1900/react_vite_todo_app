package com.example.apptodobackend   //integration test

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder



    @Test
    fun `test admin login with default credentials`() {
        // Manually creating an administrator in the test database
        val admin = User(
            username = "adminmetro@gmail.com",
            email = "adminmetro@gmail.com",
            password = passwordEncoder.encode("Metro123"),
            role = "ROLE_ADMIN"
        )
        userRepository.save(admin)

        // Creating a JSON object with the administrator's login and password
        val loginRequest = """
        {
            "email": "adminmetro@gmail.com",
            "password": "Metro123"
        }
    """.trimIndent()

        // Sending a request to log in the administrator
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isOk) // Expecting status 200 OK
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists()) // Verifying that a token is returned
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_ADMIN")) // Verifying that the role is administrator
    }





    @Test
    fun `test successful login`() {
        // Creating a user in the database for testing
        val user = User(
            username = "test45@example.com",
            email = "test45@example.com",
            password = passwordEncoder.encode("password"),
            role = "ROLE_USER"
        )
        userRepository.save(user) // Saving the user to the database

        // Creating a JSON object with the login and password
        val loginRequest = """
            {
                "email": "test45@example.com",
                "password": "password"
            }
        """.trimIndent()

        // Sending a login request
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isOk) // Expecting status 200 OK
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists()) // Verifying that a token is returned
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_USER")) // Verifying that the role is a regular user
    }

    @Test
    fun `test unsuccessful login with wrong password`() {
        // Creating a user in the database for testing
        val user = User(
            username = "test48@example.com",
            email = "test46@example.com",
            password = passwordEncoder.encode("correctPassword"), // Specifying the correct password
            role = "ROLE_USER"
        )
        userRepository.save(user) // Saving the user to the database

        // Creating a JSON object with the login and an incorrect password
        val loginRequest = """
        {
            "email": "test48@example.com",
            "password": "wrongPassword"  
        }
    """.trimIndent()

        // Sending a login request with the incorrect password
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized) //Expecting status 401 Unauthorized
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").doesNotExist()) //The token should not exist
    }

    @Test
    fun `test unsuccessful login with wrong email`() {
        //  Creating a user in the database for testing
        val user = User(
            username = "test46@example.com",
            email = "test46@example.com",
            password = passwordEncoder.encode("correctPassword"), // Specifying the correct password
            role = "ROLE_USER"
        )
        userRepository.save(user) //Saving the user to the database

        // Creating a JSON object with the login and an incorrect password
        val loginRequest = """
        {
            "email": "wrong@example.com",
            "password": "correctPassword"  
        }
    """.trimIndent()

        //Sending a login request with the incorrect password
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized) // Expecting status 401 Unauthorized
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").doesNotExist()) // The token should not exist
    }

}
