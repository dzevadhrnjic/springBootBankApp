package com.example.BankApplication.transaction.Exception;

public class TransactionIdValidation extends RuntimeException {

    public TransactionIdValidation(String message) {
        super(message);
    }
}
