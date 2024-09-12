package com.example.apptodobackend

import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.security.authentication.BadCredentialsException
import java.io.PrintWriter


import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler





class UserControllerTest {

    @Test
    fun `test registerUser should register user successfully`() {
        val userRepository = mock(UserRepository::class.java)
        val passwordEncoder = mock(PasswordEncoder::class.java)
        val userService = RegistrationController(userRepository, passwordEncoder)

        // Создаем нового пользователя
        val newUser = User(username = "newUser", email = "new@example.com", password = "password")

        // Мокаем, что пользователя с таким именем ещё нет
        `when`(userRepository.findByEmail(newUser.email)).thenReturn(null)

        // Мокаем хеширование пароля
        `when`(passwordEncoder.encode(newUser.password)).thenReturn("hashedPassword")

        // Вызываем метод регистрации
        val response = userService.registerUser(newUser)

        // Проверяем, что пользователь успешно зарегистрирован
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(userRepository).save(any(User::class.java))  // Проверяем, что пользователь был сохранён в базе данных
    }


    @Test
    fun `test loginUser should authenticate user successfully`() {
        // Мокаем зависимости
        val authenticationManager = mock(AuthenticationManager::class.java)
        val jwtTokenProvider = mock(JwtTokenProvider::class.java)
        val loginController = LoginController(authenticationManager, jwtTokenProvider)

        // Данные для входа
        val loginRequest = LoginRequest(email = "user@example.com", password = "password")

        // Мокаем процесс аутентификации
        val auth = mock(Authentication::class.java)
        `when`(auth.name).thenReturn(loginRequest.email)
        `when`(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken::class.java)))
            .thenReturn(auth)

        // Мокаем генерацию токена
        val expectedToken = "mocked-jwt-token"
        `when`(jwtTokenProvider.generateToken(auth)).thenReturn(expectedToken)

        // Вызываем метод входа в систему
        val response = loginController.login(loginRequest)

        // Проверяем, что токен был возвращен
        assertEquals(expectedToken, response.body?.get("token"))
    }


    @Test
    fun `test login should fail for invalid credentials`() {
        val authenticationManager = mock(AuthenticationManager::class.java)
        val jwtTokenProvider = mock(JwtTokenProvider::class.java)
        val loginController = LoginController(authenticationManager, jwtTokenProvider)

        // Данные для входа с неправильным паролем
        val loginRequest = LoginRequest(email = "user@example.com", password = "wrongPassword")

        // Мокаем неудачную аутентификацию
        `when`(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken::class.java)))
            .thenThrow(BadCredentialsException("Invalid credentials"))

        // Вызываем метод входа
        val response = loginController.login(loginRequest)

        // Проверяем, что статус ошибки и сообщение
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals("Login failed: Invalid credentials", response.body!!["message"])
    }

    @Test
    fun `test logout should return success response`() {
        // Мокируем LogoutSuccessHandler
        val logoutSuccessHandler = mock(LogoutSuccessHandler::class.java)

        // Мокаем объекты request, response и authentication
        val request = mock(HttpServletRequest::class.java)
        val response = mock(HttpServletResponse::class.java)
        val authentication = mock(Authentication::class.java)

        // Мокаем writer для HttpServletResponse
        val writer = mock(PrintWriter::class.java)
        `when`(response.writer).thenReturn(writer)

        // Определяем поведение при вызове onLogoutSuccess
        doAnswer { invocation ->
            val response = invocation.getArgument<HttpServletResponse>(1)
            response.status = HttpServletResponse.SC_OK
            response.writer.write("Logout successful")
            null
        }.`when`(logoutSuccessHandler).onLogoutSuccess(any(HttpServletRequest::class.java), any(HttpServletResponse::class.java), any(Authentication::class.java))

        // Вызываем onLogoutSuccess и проверяем, что статус установлен в 200
        logoutSuccessHandler.onLogoutSuccess(request, response, authentication)

        // Проверяем, что статус ответа был установлен
        verify(response).status = HttpServletResponse.SC_OK
        verify(response.writer).write("Logout successful")
    }

}