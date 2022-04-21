package com.example.BankApplication.account;

public class ValidationAccountException extends RuntimeException {
    public ValidationAccountException(String exception) {
        super(exception);
    }
}
