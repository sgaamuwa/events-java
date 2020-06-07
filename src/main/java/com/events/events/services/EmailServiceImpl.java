package com.events.events.services;

import com.events.events.models.ConfirmationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void composeVerificationEmail(String recipientEmail, ConfirmationToken confirmationToken) {
        LOGGER.info("Sending email in progress");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail);
        mailMessage.setFrom("events.gaamuwa@gmail.com");
        mailMessage.setSubject("Email Verification for Events App");
        mailMessage.setText("To confirm your account please click the link: http://localhost:8080/account/confirmAccount?token="+confirmationToken.getToken());
        try{
            javaMailSender.send(mailMessage);
        } catch(Exception exception){
            LOGGER.info("Sending email failed");
            LOGGER.error("Error when sending email = " + exception.getMessage());
            throw new MailSendException("Unable to send mail");
        }
    }
}
