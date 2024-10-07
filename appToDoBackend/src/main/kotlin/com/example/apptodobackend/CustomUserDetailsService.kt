package com.example.apptodobackend

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    private val logger: Logger = LoggerFactory.getLogger(CustomUserDetailsService::class.java)
    override fun loadUserByUsername(email: String): UserDetails {
        logger.info("Загрузка пользователя по имени: $email")

        val user = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with username: $email")

        // Converting user role to SimpleGrantedAuthority
        val authorities = listOf(SimpleGrantedAuthority(user.role))

        return org.springframework.security.core.userdetails.User(
            user.email,
            user.password,
            authorities  //Returning the list of user roles
        ).also {
            println("User ${user.email} has authorities: $authorities")
        }
    }


}
