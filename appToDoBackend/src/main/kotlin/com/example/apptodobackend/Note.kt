package com.example.apptodobackend

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import java.time.LocalDateTime
import jakarta.persistence.FetchType


@Entity
data class Note(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var title: String,
    var text: String,
    var isCompleted: Boolean = false,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var lastUpdated: LocalDateTime = LocalDateTime.now(),

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    var user: User? = null
) {
    // Adding a getter for the user's email
    val userEmail: String?
        get() = user?.email
}