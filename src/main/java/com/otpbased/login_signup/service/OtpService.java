package com.otpbased.login_signup.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.otp.expiry-minutes}")
    private int expiryMinutes;

    @Value("${app.otp.length}")
    private int otpLength;

    private static final String OTP_PREFIX = "OTP:";


    // Generate a secure random OTP
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int bound = (int) Math.pow(10, otpLength);
        int otp = random.nextInt(bound);
        // Pad with leading zeros if needed e.g. 000456
        return String.format("%0" + otpLength + "d", otp);
    }

    // Save OTP in Redis with TTL
    public void saveOtp(String emailOrPhone, String otp) {
        String key = OTP_PREFIX + emailOrPhone;
        redisTemplate.opsForValue().set(key, otp, expiryMinutes, TimeUnit.MINUTES);
    }

    // Validate OTP entered by user
    public boolean validateOtp(String emailOrPhone, String otp) {
        String key = OTP_PREFIX + emailOrPhone;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null) {
            throw new RuntimeException("OTP expired or not found. Please request a new one.");
        }

        if (!storedOtp.equals(otp)) {
            throw new RuntimeException("Invalid OTP. Please try again.");
        }

        // Delete OTP after successful validation (one-time use)
        deleteOtp(emailOrPhone);
        return true;
    }


    public void deleteOtp(String emailOrPhone) {
        redisTemplate.delete(OTP_PREFIX + emailOrPhone);
    }

}
