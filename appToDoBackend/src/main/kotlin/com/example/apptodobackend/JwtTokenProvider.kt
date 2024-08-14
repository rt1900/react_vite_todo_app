package com.example.apptodobackend

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails


@Component
class JwtTokenProvider {

    private val jwtSecret: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512) // Замените это на ваш секретный ключ
    private val jwtExpirationInMs = 3600000 // 1 час

    fun generateToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserDetails

        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs)

        val roles = userPrincipal.authorities.joinToString(",") { it.authority }

        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .claim("roles", roles)
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact()
    }

    fun getUsernameFromJWT(token: String): String {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(jwtSecret)
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject
    }

    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken)
            return true
        } catch (ex: Exception) {
            // Логирование ошибок
        }
        return false
    }
}
