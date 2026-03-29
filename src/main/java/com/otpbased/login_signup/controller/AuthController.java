package com.otpbased.login_signup.controller;

import com.otpbased.login_signup.dto.AuthResponse;
import com.otpbased.login_signup.dto.EmailOtpRequest;
import com.otpbased.login_signup.dto.VerifyOtpRequest;
import com.otpbased.login_signup.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/request-otp
    // Body: { "email": "user@example.com" }
    @PostMapping("/request-otp")
    public ResponseEntity<String> requestOtp(@RequestBody EmailOtpRequest request) {
        String message = authService.requestOtpEmail(request.getEmail());
        return ResponseEntity.ok(message);
    }

    // POST /api/auth/verify-otp
    // Body: { "email": "user@example.com", "otp": "123456" }
    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        String token = authService.verifyOtp(request.email(), request.otp());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}


// Complete Flow Summary
//        1. POST /api/auth/request-otp   { "email": "user@gmail.com" }
//         │
//                 └──► Generate OTP → Redis (TTL 5 min) → Send Email
//         ◄─── 200 OK: "OTP sent to user@gmail.com"
//
//        2. POST /api/auth/verify-otp   { "email": "user@gmail.com", "otp": "483921" }
//         │
//                 └──► Fetch OTP from Redis → Match → Delete OTP → Issue JWT
//         ◄─── 200 OK: { "token": "eyJhbGci..." }
//
//        3. GET /api/orders   Authorization: Bearer eyJhbGci...
//        │
//        └──► JwtAuthFilter validates token → Request proceeds
//         ◄─── 200 OK: [ ...orders... ]