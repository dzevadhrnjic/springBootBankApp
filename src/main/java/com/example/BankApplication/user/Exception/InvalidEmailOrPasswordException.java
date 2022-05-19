package com.example.BankApplication.user.Exception;

public class InvalidEmailOrPasswordException extends RuntimeException {

    public InvalidEmailOrPasswordException(String exception) {
        super(exception);
    }
}
