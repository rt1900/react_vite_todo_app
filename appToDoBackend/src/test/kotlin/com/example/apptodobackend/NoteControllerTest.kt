package com.example.apptodobackend

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
//
//
//
//
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("postgresql")
//class NoteControllerTest {
//
//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//    @Autowired
//    private lateinit var noteRepository: NoteRepository
//
//    @Autowired
//    private lateinit var userRepository: UserRepository
//
//    @Autowired
//    private lateinit var jwtTokenProvider: JwtTokenProvider
//
//    @BeforeEach
//    fun setup() {
//       // Очистка базы данных перед каждым тестом
//        noteRepository.deleteAll()
//        userRepository.deleteAll()
//
//        // Создаем пользователя для тестов
//       val user = User(username = "testUser", email = "test@example.com", password = "password", role = "USER")
//       userRepository.save(user)
//
//        // Создаем заметки для пользователя
//        val note1 = Note(title = "Note 1", text = "Text 1", user = user)
//        val note2 = Note(title = "Note 2", text = "Text 2", user = user)
//        noteRepository.save(note1)
//        noteRepository.save(note2)
//    }
//
//    @Test
//    fun `test getAllNotes with JWT should return status 200`() {
//
//        val userDetails = org.springframework.security.core.userdetails.User(
//            "test@example.com",
//            "password",
//            listOf(SimpleGrantedAuthority("ROLE_USER"))
//        )
//
//        val authentication: Authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
//        //val token = JwtTokenUtil.generateToken("test@example.com", "USER")
//        val token = jwtTokenProvider.generateToken(userDetails)
//
//        mockMvc.perform(get("/api/notes")
//            .header("Authorization", "Bearer $token")) // Добавляем токен в заголовок
//            .andExpect(status().isOk)
//            .andExpect(jsonPath("$.length()").value(2)) // Проверка, что вернулись 2 заметки
//    }
//
//}


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

        // Создаем объект UserDetails
//        val userDetails = org.springframework.security.core.userdetails.User(
//            "test@example.com",
//            "password",
//            listOf(SimpleGrantedAuthority("ROLE_USER"))
//        )
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
}

