package com.trackit.payment.service;

import com.trackit.payment.entity.Payment;

public interface EmailService {
    void sendPaymentConfirmationEmail(Payment payment);
    void sendPaymentFailedEmail(Payment payment, String reason);
}