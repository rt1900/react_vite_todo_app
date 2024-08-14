package com.example.apptodobackend

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity


@RestController
@RequestMapping("/api/login")
class LoginController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, String>> {
        val authenticationToken = UsernamePasswordAuthenticationToken(
            loginRequest.email,
            loginRequest.password
        )

        val authentication: Authentication = authenticationManager.authenticate(authenticationToken)
        SecurityContextHolder.getContext().authentication = authentication

        val jwtToken = jwtTokenProvider.generateToken(authentication)

        val tokenResponse = mapOf("token" to jwtToken)
        return ResponseEntity.ok(tokenResponse)
    }
}

data class LoginRequest(
    val email: String,
    val password: String,
)