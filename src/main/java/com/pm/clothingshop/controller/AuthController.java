package com.pm.clothingshop.controller;

import com.pm.clothingshop.dto.request.LoginRequest;
import com.pm.clothingshop.dto.request.UserRegisterRequest;
import com.pm.clothingshop.dto.response.AuthResponse;
import com.pm.clothingshop.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public  AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        AuthResponse authResponse = authService.register(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }
    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(new TokenValidationResponse(isValid));
    }
    private record TokenValidationResponse(boolean valid) {}
}
