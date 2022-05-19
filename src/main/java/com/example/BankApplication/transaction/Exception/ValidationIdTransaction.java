package com.example.BankApplication.transaction.Exception;

public class ValidationIdTransaction extends RuntimeException {

    public ValidationIdTransaction(String exception) {
        super(exception);
    }
}
