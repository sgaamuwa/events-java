package com.events.events.services;

import com.events.events.models.ConfirmationToken;

public interface EmailService {

    void composeVerificationEmail(String recipientEmail, ConfirmationToken confirmationToken);
}
