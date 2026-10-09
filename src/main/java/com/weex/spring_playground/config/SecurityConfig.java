package com.weex.spring_playground.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Instant;
import com.weex.spring_playground.config.common.ApiErrorResponse;
import com.weex.spring_playground.config.common.ApiSuccessResponse;

import tools.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(AbstractHttpConfigurer::disable)   // disable form login
        .httpBasic(AbstractHttpConfigurer::disable)    // disable httpBasic
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/auth/register",
                "/auth/login",
                "/auth/csrf",
                "/error",
                "/db-test",
                "/"
            )
            .permitAll()
            .anyRequest()
            .authenticated()
        )

        .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) -> {
                    ApiSuccessResponse<Void> body = new ApiSuccessResponse<>(
                            Instant.now().toString(),
                            HttpStatus.OK.value(),
                            "Logout berhasil",
                            request.getRequestURI(),
                            null
                    );

                    response.setStatus(HttpStatus.OK.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    objectMapper.writeValue(response.getWriter(), body);
                })
        )

        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(
                (request, response, authException) -> {
                    ApiErrorResponse body = new ApiErrorResponse(
                        Instant.now().toString(),
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "Unauthorized access",
                        request.getRequestURI(),
                        null
                    );

                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    objectMapper.writeValue(response.getWriter(), body);
                }
            )
        );

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
