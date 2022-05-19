package com.example.BankApplication.transaction.Exception;

public class ValidationTransactionException extends RuntimeException {

    public ValidationTransactionException(String exception) {
        super(exception);
    }
}
