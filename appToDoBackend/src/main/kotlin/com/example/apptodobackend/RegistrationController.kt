package com.example.apptodobackend

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/register")
class RegistrationController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping
    fun registerUser(@RequestBody user: User): ResponseEntity<String> {
        if (userRepository.findByEmail(user.username) != null) {
            return ResponseEntity("Username already exists", HttpStatus.BAD_REQUEST)
        }

        val newUser = User(
            username = user.username,
            password = passwordEncoder.encode(user.password), // Захешируем пароль
            email = user.email,
            role = "ROLE_USER" // Устанавливаем роль по умолчанию
        )

        userRepository.save(newUser)
        return ResponseEntity("User registered successfully", HttpStatus.OK)
    }
}
