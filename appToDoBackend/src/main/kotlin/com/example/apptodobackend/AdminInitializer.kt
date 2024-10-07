package com.example.apptodobackend

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@Profile("!test")  // This component will be disabled in the test profile
class AdminInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${spring.datasource.url}") private val dataSourceUrl: String
) : CommandLineRunner {

    override fun run(vararg args: String?) {

        if (dataSourceUrl.contains("h2")) {
            println("H2 database detected, skipping admin creation.")
            return  // If H2 is used, exit the method and do not create an administrator
        }
        val adminEmail = "adminmetro@gmail.com"
        val adminPassword = "Metro123"

        // Checking for the existence of an administrator in the database
        if (userRepository.findByEmail(adminEmail) == null) {
            //If the administrator is not present, create one
            val adminUser = User(
                email = adminEmail,
                username = adminEmail,
                password = passwordEncoder.encode(adminPassword), // Hashed password
                role = "ROLE_ADMIN"
            )
            userRepository.save(adminUser)
            println("Admin user created with email: $adminEmail")
        } else {
            println("Admin user already exists in the database")
        }
    }
}