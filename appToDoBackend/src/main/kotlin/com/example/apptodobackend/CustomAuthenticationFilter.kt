package com.example.apptodobackend
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletResponse

class CustomAuthenticationFilter(
    authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider
) : UsernamePasswordAuthenticationFilter(authenticationManager) {

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(
        request: jakarta.servlet.http.HttpServletRequest,
        response: jakarta.servlet.http.HttpServletResponse
    ): Authentication {
        println("Attempting authentication for request: ${request.requestURI}")
        val objectMapper = ObjectMapper()
        val loginRequest = objectMapper.readValue(request.inputStream, LoginRequest::class.java)
        val authRequest = UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
        return authenticationManager.authenticate(authRequest)
    }

    override fun successfulAuthentication(
        request: jakarta.servlet.http.HttpServletRequest,
        response: jakarta.servlet.http.HttpServletResponse,
        chain: jakarta.servlet.FilterChain,
        authResult: Authentication
    ) {
        SecurityContextHolder.getContext().authentication = authResult
        println("User ${authResult.name} successfully authenticated with authorities: ${authResult.authorities}")

        // Генерация JWT токена
        logger.info("User ${authResult.name} successfully authenticated")
        val token = jwtTokenProvider.generateToken(authResult)
        logger.info("JWT token generated: $token")



        response.addHeader("Authorization", "Bearer $token")

        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write("{\"token\": \"$token\"}")
    }

    override fun unsuccessfulAuthentication(
        request: jakarta.servlet.http.HttpServletRequest,
        response: jakarta.servlet.http.HttpServletResponse,
        failed: AuthenticationException

    ) {
        logger.warn("Authentication failed: ${failed.message}")

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write("{\"message\": \"Login failed: ${failed.message}\"}")

    }
}
