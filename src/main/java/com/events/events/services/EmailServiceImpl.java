package com.events.events.services;

import com.events.events.models.ConfirmationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void composeVerificationEmail(String recipientEmail, ConfirmationToken confirmationToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject("Email Verification for Events App");
        mailMessage.setText("To confirm your account please click the link: http://localhost:8080/account/confirmAccount?token="+confirmationToken.getToken());
        javaMailSender.send(mailMessage);
    }
}
