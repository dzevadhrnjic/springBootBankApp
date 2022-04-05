package com.example.BankApplication.user;

public class InvalidEmailOrPasswordException extends RuntimeException{

    public InvalidEmailOrPasswordException(String exception) {
            super(exception);
    }
}
