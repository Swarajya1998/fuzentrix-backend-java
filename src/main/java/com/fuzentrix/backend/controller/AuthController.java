package com.fuzentrix.backend.controller;

import com.fuzentrix.backend.dto.AuthDto;
import com.fuzentrix.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody AuthDto.LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        authService.login(request, httpRequest, httpResponse);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody AuthDto.RegisterRequest request
    ) {
        authService.register(request);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.refreshToken(request, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.logout(request, response);
        return ResponseEntity.ok().build();
    }
}
