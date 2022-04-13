package com.example.BankApplication.transaction;

import java.sql.SQLException;

public class TransactionValidationService {

    public static AmountService amountService = new AmountService();

    public static void transactionFieldsValidation(String token, Transaction transaction, Long accountId) throws SQLException{
        amountValidation(transaction);
        sourceAccountValidation(token,accountId, transaction);

    }


    public static void amountValidation(Transaction transaction) {
    if (transaction.getAmount() == null){
        throw new ValidationTransactionException("Amount can't be null");
    }else if (transaction.getAmount() <= 0){
            throw new ValidationTransactionException("Amount need to be more than zero");
        }
    }

    public static void sourceAccountValidation(String token,Long accountId, Transaction transaction) throws SQLException {
        if (amountService.accountIncome(accountId) < transaction.getAmount()){
            throw new ValidationTransactionException("You don't have that amount on your account");
        }
    }
}
