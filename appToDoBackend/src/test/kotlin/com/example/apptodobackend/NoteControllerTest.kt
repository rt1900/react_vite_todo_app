package com.example.apptodobackend      //integration test

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.http.MediaType


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("postgresql")
class NoteControllerTest {

    @Autowired
    private lateinit var customUserDetailsService: CustomUserDetailsService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var noteRepository: NoteRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        // Очистка базы данных перед каждым тестом
        noteRepository.deleteAll()
        userRepository.deleteAll()

        // Создаем пользователя для тестов
        val user = User(username = "test@example.com", email = "test@example.com", password = "password", role = "ROLE_USER")
        userRepository.save(user)


        // Создаем заметки для пользователя
        val note1 = Note(title = "Note 1", text = "Text 1", user = user)
        val note2 = Note(title = "Note 2", text = "Text 2", user = user)
        noteRepository.save(note1)
        noteRepository.save(note2)
    }

    @Test
    fun `test getAllNotes with JWT should return status 200`() {

        val userDetails = customUserDetailsService.loadUserByUsername("test@example.com")

        // Создаем объект Authentication на основе UserDetails
        val authentication: Authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

        // Генерируем токен
        val token = jwtTokenProvider.generateToken(authentication)

        mockMvc.perform(get("/api/notes")
            .header("Authorization", "Bearer $token")) // Добавляем токен в заголовок
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2)) // Проверка, что вернулись 2 заметки
    }

    @Test
    fun `test create note through API`() {
        // Создание нового пользователя
        val user = User(username = "testUser", email = "test1@example.com", password = "password", role = "ROLE_USER")
        userRepository.save(user)

        // Логинимся и получаем токен
        val userDetails = customUserDetailsService.loadUserByUsername("test1@example.com")
        val authentication: Authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        val token = jwtTokenProvider.generateToken(authentication)

        // Данные новой заметки
        val newNote = mapOf("title" to "New Note", "text" to "Note content")

        // Отправляем запрос на создание новой заметки
        mockMvc.perform(post("/api/notes")
            .header("Authorization", "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newNote)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("New Note"))
            .andExpect(jsonPath("$.text").value("Note content"))
    }

    @Test
    fun `test update note through API`() {
        // Создание нового пользователя
        val user = User(username = "testUser", email = "test2@example.com", password = "password", role = "ROLE_USER")
        userRepository.save(user)

        // Логинимся и получаем токен
        val userDetails = customUserDetailsService.loadUserByUsername("test2@example.com")
        val authentication: Authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        val token = jwtTokenProvider.generateToken(authentication)

        // Создаем заметку
        val note = Note(title = "Original Title", text = "Original Content", user = user)
        noteRepository.save(note)

        // Данные для обновления
        val updatedNote = mapOf("title" to "Updated Title", "text" to "Updated Content")

        // Отправляем запрос на обновление заметки
        mockMvc.perform(put("/api/notes/${note.id}")  // Путь к конкретной заметке
            .header("Authorization", "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedNote)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.text").value("Updated Content"))
    }


    @Test
    fun `test delete note through API`() {
        // Создание нового пользователя
        val user = User(username = "testUser", email = "test3@example.com", password = "password", role = "ROLE_USER")
        userRepository.save(user)

        // Логинимся и получаем токен
        val userDetails = customUserDetailsService.loadUserByUsername("test3@example.com")
        val authentication: Authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        val token = jwtTokenProvider.generateToken(authentication)

        // Создаем и сохраняем заметку
        val note = Note(title = "Title to be deleted", text = "Content to be deleted", user = user)
        val savedNote = noteRepository.save(note)  // Сохраняем заметку в базу и получаем её ID

        // Убеждаемся, что заметка сохранена
        mockMvc.perform(get("/api/notes/${savedNote.id}")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)

        // Отправляем запрос на удаление заметки
        mockMvc.perform(delete("/api/notes/${savedNote.id}")  // Используем ID сохраненной заметки
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isNoContent)  // Ожидаем статус 204 для успешного удаления

        // Проверяем, что заметка была удалена (попытка получить ее возвращает 404)
        mockMvc.perform(get("/api/notes/${savedNote.id}")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isNotFound)  // Ожидаем статус 404 после удаления

    }

    @Test
    fun `test get note by ID through API`() {
        // Создание нового пользователя
        val user = User(username = "testUser", email = "test4@example.com", password = "password", role = "ROLE_USER")
        userRepository.save(user)

        // Логинимся и получаем токен
        val userDetails = customUserDetailsService.loadUserByUsername("test4@example.com")
        val authentication: Authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        val token = jwtTokenProvider.generateToken(authentication)

        // Создаем и сохраняем заметку
        val note = Note(title = "Test Note", text = "This is a test note.", user = user)
        val savedNote = noteRepository.save(note)  // Сохраняем заметку в базу и получаем её ID

        // Выполняем GET запрос для получения заметки по её ID
        mockMvc.perform(get("/api/notes/${savedNote.id}")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)  // Ожидаем статус 200 OK
            .andExpect(jsonPath("$.title").value("Test Note"))  // Проверяем, что заголовок совпадает
            .andExpect(jsonPath("$.text").value("This is a test note."))  // Проверяем текст заметки
            .andExpect(jsonPath("$.user.email").value(user.email))  // Проверяем, что пользователь совпадает
    }


}

