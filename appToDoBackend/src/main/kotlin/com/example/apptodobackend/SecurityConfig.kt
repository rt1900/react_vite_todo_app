package com.example.apptodobackend

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(val userDetailsService: UserDetailsService) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
    @Bean
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authenticationManagerBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
        return authenticationManagerBuilder.build()
    }


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val jwtTokenProvider = JwtTokenProvider() // Создааю или получаю экземпляр JwtTokenProvider
        http
            .csrf { csrf -> csrf.disable() }
//            .authorizeHttpRequests { authz ->
//                authz
//                    .requestMatchers("/**").permitAll()  // Разрешает доступ ко всем маршрутам без аутентификации
//            }

            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/user/**", "/api/notes/**").hasRole("USER")
                    .requestMatchers("/", "/api/register", "/api/login").permitAll()
            }
            .addFilterBefore(CustomAuthenticationFilter(authenticationManager(http), jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
//            .formLogin { form ->
//                form
//                    .loginProcessingUrl("/api/login")
//                    .defaultSuccessUrl("/api/notes", true)
//                    .permitAll()
//            }
            .logout { logout ->
                logout
                    .logoutUrl("/api/logout")
                    .permitAll()
            }

        return http.build()
    }

}
