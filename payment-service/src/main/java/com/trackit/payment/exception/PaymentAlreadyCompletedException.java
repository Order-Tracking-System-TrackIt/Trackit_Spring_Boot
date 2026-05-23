package com.trackit.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PaymentAlreadyCompletedException extends PaymentException {
    
    public PaymentAlreadyCompletedException(String orderId) {
        super("Payment is already completed for order ID: " + orderId);
    }
}