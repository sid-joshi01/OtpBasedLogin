package com.otpbased.login_signup.controller;

import com.otpbased.login_signup.dto.*;
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

    // POST /api/auth/email/request-otp
    // Body: { "email": "user@example.com" }
    @PostMapping("/email/request-otp")
    public ResponseEntity<String> requestOtp(@RequestBody EmailOtpRequest request) {
        String message = authService.requestOtpEmail(request.getEmail());
        return ResponseEntity.ok(message);
    }

    // POST /api/auth/email/verify-otp
    // Body: { "email": "user@example.com", "otp": "123456" }
    @PostMapping("/email/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        String token = authService.verifyOtp(request.email(), request.otp());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    // POST /api/auth/request-otp
    // Body: { "phone": "9855248612" }
    @PostMapping("/phone/request-otp")
    public ResponseEntity<String> requestSmsOtp(@RequestBody PhoneOtpRequest request) {
        String message = authService.requestSmsOtp(request.toNumber());
        return ResponseEntity.ok(message);
    }

    // POST /api/auth/phone/verify-otp
    // Body: { "phone": "9855248612", "otp": "123456" }
    @PostMapping("/phone/verify-otp")
    public ResponseEntity<AuthResponse> verifySmsOtp(@RequestBody VerifyPhoneOtpRequest request) {
        String token = authService.verifySmsOtp(request.toNumber(), request.otp());
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