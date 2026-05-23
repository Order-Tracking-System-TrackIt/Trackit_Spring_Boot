package com.trackit.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedPaymentException extends PaymentException {
    
    public UnauthorizedPaymentException(String message) {
        super(message);
    }
}