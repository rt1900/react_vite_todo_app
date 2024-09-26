package com.example.apptodobackend  //integr,Tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(properties = ["spring.config.name=application-test"])
@Transactional
@Rollback
class UserAccessIntegrationTest {

    @Autowired
    private lateinit var noteRepository: NoteRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `admin should have access to all notes`() {
        // Создаем пользователя и несколько заметок
        val user1 = User(username = "user1", email = "user1@example.com", password = "password123", role = "USER")
        val user2 = User(username = "user2", email = "user2@example.com", password = "password456", role = "USER")
        userRepository.save(user1)
        userRepository.save(user2)

        val note1 = Note(title = "User1's Note", text = "User1's content", user = user1)
        val note2 = Note(title = "User2's Note", text = "User2's content", user = user2)
        noteRepository.save(note1)
        noteRepository.save(note2)

        // Админ должен иметь доступ ко всем заметкам
        val allNotes = noteRepository.findAll()
        assertEquals(2, allNotes.size)
        assertTrue(allNotes.any { it.user == user1 })
        assertTrue(allNotes.any { it.user == user2 })
    }

    @Test
    @WithMockUser(username = "user1", roles = ["USER"])
    fun `user should only have access to their own notes`() {
        // Создаем пользователя и несколько заметок
        val user1 = User(username = "user1", email = "user1@example.com", password = "password123", role = "USER")
        val user2 = User(username = "user2", email = "user2@example.com", password = "password456", role = "USER")
        userRepository.save(user1)
        userRepository.save(user2)

        val note1 = Note(title = "User1's Note", text = "User1's content", user = user1)
        val note2 = Note(title = "User2's Note", text = "User2's content", user = user2)
        noteRepository.save(note1)
        noteRepository.save(note2)

        // Пользователь должен иметь доступ только к своим заметкам
        val user1Notes = noteRepository.findByUser(user1)
        assertEquals(1, user1Notes.size)
        assertEquals("User1's Note", user1Notes[0].title)

        // Проверяем, что пользователь не видит заметки другого пользователя
        val user2Notes = noteRepository.findByUser(user2)
        assertTrue(user2Notes.none { it.user == user1 })
    }
}
