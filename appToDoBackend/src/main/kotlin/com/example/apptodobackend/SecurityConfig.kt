package com.example.apptodobackend

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import jakarta.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
class SecurityConfig(val customUserDetailsService: CustomUserDetailsService) {
    private val logger: Logger = LoggerFactory.getLogger(SecurityConfig::class.java)

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
    @Bean
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authenticationManagerBuilder
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder())
        return authenticationManagerBuilder.build()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        logger.info("Configuring the security filter chain")


        val jwtTokenProvider = JwtTokenProvider()  // do I need this?
        http
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/user/**", "/api/notes/**").hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/", "/api/register", "/api/login", "/api/logout").permitAll()
//                    .requestMatchers("/api/logout").authenticated()
            }
            .addFilterBefore(JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService), UsernamePasswordAuthenticationFilter::class.java)
            //.addFilterBefore(CustomAuthenticationFilter(authenticationManager(http), jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)   // do I need this?

            .logout { logout ->
                logout
                    .logoutUrl("/api/logout")
                    .logoutSuccessHandler { request, response, authentication ->
                        response.status = HttpServletResponse.SC_OK
                        response.writer.write("Logout successful")
                        response.writer.flush()
                    }
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            }

        return http.build()
    }
}
