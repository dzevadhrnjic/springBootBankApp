package com.example.BankApplication.account;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String exception) {
        super(exception);
    }
}
