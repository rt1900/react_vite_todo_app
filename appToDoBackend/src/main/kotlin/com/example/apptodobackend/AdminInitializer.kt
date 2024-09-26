package com.example.apptodobackend

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@Profile("!test")  // Этот компонент будет отключен в тестовом профиле
class AdminInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${spring.datasource.url}") private val dataSourceUrl: String
) : CommandLineRunner {

    override fun run(vararg args: String?) {

        if (dataSourceUrl.contains("h2")) {
            println("H2 database detected, skipping admin creation.")
            return  // Если используется H2, выходим из метода и не создаем администратора
        }
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