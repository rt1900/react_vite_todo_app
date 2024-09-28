package com.example.apptodobackend   //integration test

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder



    @Test
    fun `test admin login with default credentials`() {
        // 1. Создаем администратора вручную в тестовой базе данных
        val admin = User(
            username = "adminmetro@gmail.com",
            email = "adminmetro@gmail.com",
            password = passwordEncoder.encode("Metro123"),
            role = "ROLE_ADMIN"
        )
        userRepository.save(admin)

        // 2. Создаем JSON объект с логином и паролем администратора
        val loginRequest = """
        {
            "email": "adminmetro@gmail.com",
            "password": "Metro123"
        }
    """.trimIndent()

        // 3. Отправляем запрос на логин администратора
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isOk) // Ожидаем статус 200 OK
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists()) // Проверяем, что возвращается токен
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_ADMIN")) // Проверяем, что роль — администратор
    }





    @Test
    fun `test successful login`() {
        // 1. Создаем пользователя в базе данных для теста
        val user = User(
            username = "test45@example.com",
            email = "test45@example.com",
            password = passwordEncoder.encode("password"),
            role = "ROLE_USER"
        )
        userRepository.save(user) // Сохраняем пользователя в базе данных

        // 2. Создаем JSON объект с логином и паролем
        val loginRequest = """
            {
                "email": "test45@example.com",
                "password": "password"
            }
        """.trimIndent()

        // 3. Отправляем запрос на логин
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isOk) // Ожидаем статус 200 OK
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists()) // Проверяем, что возвращается токен
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_USER")) // Проверяем, что роль - обычный пользователь
    }

    @Test
    fun `test unsuccessful login with wrong password`() {
        // 1. Создаем пользователя в базе данных для теста
        val user = User(
            username = "test48@example.com",
            email = "test46@example.com",
            password = passwordEncoder.encode("correctPassword"), // Указываем правильный пароль
            role = "ROLE_USER"
        )
        userRepository.save(user) // Сохраняем пользователя в базе данных

        // 2. Создаем JSON объект с логином и неверным паролем
        val loginRequest = """
        {
            "email": "test48@example.com",
            "password": "wrongPassword"  
        }
    """.trimIndent()

        // 3. Отправляем запрос на логин с неправильным паролем
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized) // Ожидаем статус 401 Unauthorized
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").doesNotExist()) // Токен не должен существовать
    }

    @Test
    fun `test unsuccessful login with wrong email`() {
        // 1. Создаем пользователя в базе данных для теста
        val user = User(
            username = "test46@example.com",
            email = "test46@example.com",
            password = passwordEncoder.encode("correctPassword"), // Указываем правильный пароль
            role = "ROLE_USER"
        )
        userRepository.save(user) // Сохраняем пользователя в базе данных

        // 2. Создаем JSON объект с логином и неверным паролем
        val loginRequest = """
        {
            "email": "wrong@example.com",
            "password": "correctPassword"  
        }
    """.trimIndent()

        // 3. Отправляем запрос на логин с неправильным паролем
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized) // Ожидаем статус 401 Unauthorized
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").doesNotExist()) // Токен не должен существовать
    }

}
