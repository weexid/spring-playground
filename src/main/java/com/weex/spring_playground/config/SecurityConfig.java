package com.weex.spring_playground.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

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
                    response.setStatus(200);
                    response.setContentType(
                            MediaType.APPLICATION_JSON_VALUE
                    );
                    response.getWriter()
                            .write("{\"message\":\"Logout berhasil\"}");
                })
        )

        .exceptionHandling(exception -> exception
                .authenticationEntryPoint(
                        (request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType(
                                    MediaType.APPLICATION_JSON_VALUE
                            );
                            response.getWriter()
                                    .write("{\"message\":\"Anda belum login\"}");
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
