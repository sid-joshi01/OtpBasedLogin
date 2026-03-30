package com.otpbased.login_signup.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${app.twilio.sid}")
    private String twilioId;

    @Value("${app.twilio.token}")
    private String authToken;

    @Value("${app.twilio.phone}")
    private String fromNumber;

    @PostConstruct
    public void init(){
        Twilio.init(twilioId, authToken);
    }

    public void sendOtp(String toNumber, String otp){
        if (!toNumber.startsWith("+")) {
            toNumber = "+91" + toNumber; // assuming India
        }

        String messageBody = "Your OTP is " + otp + ". Do not share it.";
        System.out.println("From number: " + fromNumber);
        System.out.println("To number: " + toNumber);
        Message.creator(new PhoneNumber(toNumber), new PhoneNumber(fromNumber), messageBody).create();
    }
}
