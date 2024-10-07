package com.example.apptodobackend

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: CustomUserDetailsService // Service for loading user data
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.info("The JWT token validation is starting")
        val token = getJwtFromRequest(request)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            logger.info("The JWT token is valid")
            val username = jwtTokenProvider.getUsernameFromJWT(token)
            logger.info("Username extracted from the token: $username")
            val userDetails = userDetailsService.loadUserByUsername(username)
            logger.info("User details have been loaded: ${userDetails.username}")

            val authentication = UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.authorities
            )
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
        }else {
            logger.warn("JWT token not found or invalid")
        }
        logger.info("Passing the request to the next filter")

        filterChain.doFilter(request, response)
    }

    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }
}
