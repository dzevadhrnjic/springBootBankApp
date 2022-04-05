package com.example.BankApplication.transaction;

public class ValidationTransactionException extends RuntimeException {

    public ValidationTransactionException(String exception) {
        super(exception);
    }
}
