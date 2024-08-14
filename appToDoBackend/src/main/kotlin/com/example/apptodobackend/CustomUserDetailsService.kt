package com.example.apptodobackend

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with username: $email")

        // Преобразуем роль пользователя в SimpleGrantedAuthority
        val authorities = listOf(SimpleGrantedAuthority(user.role))

        return org.springframework.security.core.userdetails.User(
            user.email,
            user.password,
            authorities  // Возвращаем список ролей пользователя
        ).also {
            println("User ${user.email} has authorities: $authorities")
        }
    }
}
