package com.example.BankApplication.transaction;

public class ValidationIdTransaction extends RuntimeException{

    public ValidationIdTransaction(String exception) {
        super(exception);
    }
}
