package com.otpbased.login_signup.service;

import com.otpbased.login_signup.entity.User;
import com.otpbased.login_signup.repository.UserRepository;
import com.otpbased.login_signup.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    // Step 1: Request OTP
    public String requestOtpEmail(String email) {
        // Auto-register if user doesn't exist (optional)
        userRepository.findByEmail(email).orElseGet(() ->
                userRepository.save(User.builder()
                        .email(email)
                        .verified(false)
                        .build())
        );

        String otp = otpService.generateOtp();
        otpService.saveOtp(email, otp);
        emailService.sendOtp(email, otp);

        return "OTP sent to " + email;
    }

    // Step 2: Verify OTP and return JWT
    public String verifyOtp(String email, String otp) {
        // Throws exception if OTP is invalid or expired
        otpService.validateOtp(email, otp);

        // Mark user as verified
        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);
        userRepository.save(user);

        // Generate and return JWT
        return jwtUtil.generateToken(email);
    }
}
