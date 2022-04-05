package com.example.BankApplication.transaction;

public class TransactionIdValidation extends RuntimeException{

    public TransactionIdValidation(String message) {
        super(message);
    }
}
