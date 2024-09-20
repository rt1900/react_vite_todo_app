package com.example.apptodobackend

import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AdminInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val adminEmail = "adminmetro@gmail.com"
        val adminPassword = "Metro123"

        // Проверяем, существует ли администратор в базе данных
        if (userRepository.findByEmail(adminEmail) == null) {
            // Если администратора нет, создаём его
            val adminUser = User(
                email = adminEmail,
                username = adminEmail,
                password = passwordEncoder.encode(adminPassword), // Захешированный пароль
                role = "ROLE_ADMIN"
            )
            userRepository.save(adminUser)
            println("Admin user created with email: $adminEmail")
        } else {
            println("Admin user already exists in the database")
        }
    }
}