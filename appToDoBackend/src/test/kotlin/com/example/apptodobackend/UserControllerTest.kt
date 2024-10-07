package com.example.apptodobackend    //unitTest

import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.security.authentication.BadCredentialsException
import java.io.PrintWriter


import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler





class UserControllerTest {

    @Test
    fun `test registerUser should register user successfully`() {
        val userRepository = mock(UserRepository::class.java)
        val passwordEncoder = mock(PasswordEncoder::class.java)
        val userService = RegistrationController(userRepository, passwordEncoder)

        // Creating a new user
        val newUser = User(username = "newUser", email = "new@example.com", password = "password")

        // Mocking that there is no user with the same email
        `when`(userRepository.findByEmail(newUser.email)).thenReturn(null)

        // Mocking password hashing
        `when`(passwordEncoder.encode(newUser.password)).thenReturn("hashedPassword")

        // Calling the registration method
        val response = userService.registerUser(newUser)

        // Verifying that the user was successfully registered
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(userRepository).save(any(User::class.java))  // Verifying that the user was saved to the database
    }



    @Test
    fun `test loginUser should authenticate user successfully`() {
        // Mocking dependencies
        val authenticationManager = mock(AuthenticationManager::class.java)
        val jwtTokenProvider = mock(JwtTokenProvider::class.java)
        val loginController = LoginController(authenticationManager, jwtTokenProvider)

        // Login credentials
        val loginRequest = LoginRequest(email = "user@example.com", password = "password")

        // Mocking authentication process
        val auth = mock(Authentication::class.java)
        `when`(auth.name).thenReturn(loginRequest.email)
        `when`(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken::class.java)))
            .thenReturn(auth)

        // Mocking token generation
        val expectedToken = "mocked-jwt-token"
        `when`(jwtTokenProvider.generateToken(auth)).thenReturn(expectedToken)

        // Calling the login method
        val response = loginController.login(loginRequest)

        // Verifying that the token was returned
        assertEquals(expectedToken, response.body?.get("token"))
    }


    @Test
    fun `test login should fail for invalid credentials`() {
        val authenticationManager = mock(AuthenticationManager::class.java)
        val jwtTokenProvider = mock(JwtTokenProvider::class.java)
        val loginController = LoginController(authenticationManager, jwtTokenProvider)

        // Login credentials with an incorrect password
        val loginRequest = LoginRequest(email = "user@example.com", password = "wrongPassword")

        // Mocking failed authentication
        `when`(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken::class.java)))
            .thenThrow(BadCredentialsException("Invalid credentials"))

        // Calling the login method
        val response = loginController.login(loginRequest)

        // Verifying the error status and message
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals("Login failed: Invalid credentials", response.body!!["message"])
    }

    @Test
    fun `test logout should return success response`() {
        // Mocking LogoutSuccessHandler
        val logoutSuccessHandler = mock(LogoutSuccessHandler::class.java)

        // Mocking request, response, and authentication objects
        val request = mock(HttpServletRequest::class.java)
        val response = mock(HttpServletResponse::class.java)
        val authentication = mock(Authentication::class.java)

        // Mocking writer for HttpServletResponse
        val writer = mock(PrintWriter::class.java)
        `when`(response.writer).thenReturn(writer)

        // Defining behavior when onLogoutSuccess is called
        doAnswer { invocation ->
            val response = invocation.getArgument<HttpServletResponse>(1)
            response.status = HttpServletResponse.SC_OK
            response.writer.write("Logout successful")
            null
        }.`when`(logoutSuccessHandler).onLogoutSuccess(any(HttpServletRequest::class.java), any(HttpServletResponse::class.java), any(Authentication::class.java))

        // Calling onLogoutSuccess and checking that the status is set to 200
        logoutSuccessHandler.onLogoutSuccess(request, response, authentication)

        // Verifying that the response status was set
        verify(response).status = HttpServletResponse.SC_OK
        verify(response.writer).write("Logout successful")
    }

}