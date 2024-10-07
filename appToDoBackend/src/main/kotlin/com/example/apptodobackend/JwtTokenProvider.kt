package com.example.apptodobackend

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.jsonwebtoken.security.Keys
import javax.crypto.SecretKey


@Component
class JwtTokenProvider {

    private val logger: Logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    private val jwtSecretKey: SecretKey = Keys.hmacShaKeyFor("SuperSecretKeyThatIsNotVeryLongAndNotVerySecureAndMaybeMeetsThe512BitRequirementMercedesIsBetterThanBMW".toByteArray())
    private val jwtExpirationInMs = 3600000

    fun generateToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserDetails

        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs)

        val claims: Claims = Jwts.claims().setSubject(userPrincipal.username)
        claims["roles"] = userPrincipal.authorities.joinToString(",") { it.authority }

        return Jwts.builder()
            .setClaims(claims)
            .setIssuer("IssuerName") // Set the Issuer value
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(jwtSecretKey, SignatureAlgorithm.HS512)  // Using HS256 instead of HS512
            .compact()
    }

    fun getUsernameFromJWT(token: String): String {
        logger.info("Extracting username from the JWT token")
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(jwtSecretKey)
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject
    }

    fun validateToken(authToken: String): Boolean {
        try {
            logger.info("Validating the token.")
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(authToken)
            return true
        } catch (ex: Exception) {
            logger.error("Error during token validation: ${ex.message}")
        }
        return false
    }

    private fun getClaimsFromToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(jwtSecretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }

}