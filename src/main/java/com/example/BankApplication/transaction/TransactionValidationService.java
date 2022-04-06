package com.example.BankApplication.transaction;

import java.sql.SQLException;



public class TransactionValidationService {


    public static void transactionFieldsValidation(Transaction transaction, Long accountId, AmountService amountService) throws SQLException{
        amountValidation(transaction);
        sourceAccountValidation(accountId, transaction, amountService);
    }

    public static void amountValidation(Transaction transaction) {
    if (transaction.getAmount() == null){
        throw new ValidationTransactionException("Amount can't be null");
    }else if (transaction.getAmount() <= 0){
            throw new ValidationTransactionException("Amount need to be more than zero");
        }
    }

    public static void sourceAccountValidation(Long accountId, Transaction transaction, AmountService amountService) throws SQLException {
        if (amountService.accountIncome(accountId) < transaction.getAmount()){
            throw new ValidationTransactionException("You don't have that amount on your account");
        }
    }


}
