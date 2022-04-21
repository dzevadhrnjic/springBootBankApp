package com.example.BankApplication.user;

public class ValidationException extends RuntimeException {

    public ValidationException(String runtimeException) {
        super(runtimeException);
    }

}
