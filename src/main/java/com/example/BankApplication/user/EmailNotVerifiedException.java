package com.example.BankApplication.user;

public class EmailNotVerifiedException extends RuntimeException{

    public EmailNotVerifiedException(String runtimeException) {
        super(runtimeException);
    }
}
