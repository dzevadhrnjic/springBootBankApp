package com.example.BankApplication.user;

public class JWTCreationException extends RuntimeException {

    public JWTCreationException(String exception) {
        super(exception);
    }
}
