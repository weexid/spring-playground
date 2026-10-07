package com.weex.spring_playground.auth;

import com.weex.spring_playground.config.common.ConflictException;
import com.weex.spring_playground.user.User;
import com.weex.spring_playground.user.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        // trim email
        String email = request.email().trim().toLowerCase(), username = request.username().trim();

        if (userRepository.existsByEmail(email) || userRepository.existsByUsername(username)) {
            throw new ConflictException("username or email already used!");
        }

        User user = new User(
                username,
                email,
                passwordEncoder.encode(request.password())
        );

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Register berhasil"
                ));
    }

    public record RegisterRequest(
            @NotBlank @Size(min = 2, max = 100) String username,
            @Email @NotBlank String email,
            @NotBlank @Size(min = 6) String password
    ) {}
}