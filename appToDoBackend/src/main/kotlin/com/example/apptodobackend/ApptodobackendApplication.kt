package com.example.apptodobackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.example.apptodobackend"]) // Добавьте эту строку, если ее нет
class ApptodobackendApplication

fun main(args: Array<String>) {
	runApplication<ApptodobackendApplication>(*args)
}
