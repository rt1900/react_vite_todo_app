package com.example.apptodobackend

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface NoteRepository : JpaRepository<Note, Long> {
    fun findByUser(user: User): List<Note>
}
