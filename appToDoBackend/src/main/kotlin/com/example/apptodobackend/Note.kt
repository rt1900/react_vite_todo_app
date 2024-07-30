package com.example.apptodobackend

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime //r

@Entity
data class Note(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var title: String, // новое поле
    var text: String,
    var isCompleted: Boolean = false,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var lastUpdated: LocalDateTime = LocalDateTime.now() //r

)
