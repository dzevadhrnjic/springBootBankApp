package com.example.BankApplication.user.Exception;

public class EmailNotVerifiedException extends RuntimeException{

    public EmailNotVerifiedException(String runtimeException) {
        super(runtimeException);
    }
}
