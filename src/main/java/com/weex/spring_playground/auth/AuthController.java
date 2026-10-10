package com.weex.spring_playground.auth;

import com.weex.spring_playground.config.common.ApiSuccessResponse;
import com.weex.spring_playground.config.common.ConflictException;
import com.weex.spring_playground.user.User;
import com.weex.spring_playground.user.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.csrf.CsrfToken;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



import java.time.Instant;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiSuccessResponse<Void>> register(
            @Valid @RequestBody RegisterRequest request, HttpServletRequest req
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
        var body = new ApiSuccessResponse<Void>(
                Instant.now().toString(),
                HttpStatus.CREATED.value(),
                "Successfully registered",
                req.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(body);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiSuccessResponse<Void>>login(
        @Valid @RequestBody LoginRequest request,
        HttpServletRequest req
    ) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.email().trim().toLowerCase(),
                    request.password()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = req.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            var body = new ApiSuccessResponse<Void>(
                    Instant.now().toString(),
                    HttpStatus.OK.value(),
                    "Successfully logged in",
                    req.getRequestURI(),
                    null
            );

            return ResponseEntity.ok(body);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Email or password not match");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiSuccessResponse<MeResponse>> me(
        Authentication authentication,
        HttpServletRequest req
    ) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var body = new ApiSuccessResponse<>(
                Instant.now().toString(),
                HttpStatus.OK.value(),
                "User retrieved",
                req.getRequestURI(),
                new MeResponse(user.getId(), user.getUsername(), user.getEmail())
        );

        return ResponseEntity.ok(body);
    }

    public record RegisterRequest(
            @NotBlank @Size(min = 2, max = 100) String username,
            @Email @NotBlank String email,
            @NotBlank @Size(min = 6) String password
    ) {}

    public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
    ) {}

    public record MeResponse(
        Long id,
        String username,
        String email
    ) {}
}