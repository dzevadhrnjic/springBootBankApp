package com.example.BankApplication.user.Exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String runtimeException) {
        super(runtimeException);
    }

}
