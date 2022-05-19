package com.example.BankApplication.account.Exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String exception) {
        super(exception);
    }
}
