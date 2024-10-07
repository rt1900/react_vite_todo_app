package com.example.apptodobackend

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.http.HttpStatus



@RestController
@RequestMapping("/api/login")
class LoginController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, String?>> {
        return try {
            val authenticationToken = UsernamePasswordAuthenticationToken(
                loginRequest.email,
                loginRequest.password
            )

            val authentication: Authentication = authenticationManager.authenticate(authenticationToken)
            SecurityContextHolder.getContext().authentication = authentication

            val jwtToken = jwtTokenProvider.generateToken(authentication)
            val role = authentication.authorities.firstOrNull()?.authority      // Getting the user role

            // Adding both the token and the role to the response
            val tokenResponse: Map<String, String?> = mapOf(
                "token" to jwtToken,
                "role" to role
            )

            ResponseEntity.ok(tokenResponse)
        } catch (ex: BadCredentialsException) {
            val errorResponse: Map<String, String?> = mapOf("message" to "Login failed: Invalid credentials")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
        }
    }


}

data class LoginRequest(
    val email: String,
    val password: String,
)