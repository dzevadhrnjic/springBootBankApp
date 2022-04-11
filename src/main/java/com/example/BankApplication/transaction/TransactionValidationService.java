package com.example.BankApplication.transaction;

import com.example.BankApplication.user.TokenUtil;

import java.sql.SQLException;

public class TransactionValidationService {

    public static TokenUtil tokenUtil = new TokenUtil();

    public static void transactionFieldsValidation(String token,Transaction transaction, Long accountId, AmountService amountService) throws SQLException{
        amountValidation(transaction);
        sourceAccountValidation(token,accountId, transaction, amountService);
    }


    public static void amountValidation(Transaction transaction) {
    if (transaction.getAmount() == null){
        throw new ValidationTransactionException("Amount can't be null");
    }else if (transaction.getAmount() <= 0){
            throw new ValidationTransactionException("Amount need to be more than zero");
        }
    }

    public static void sourceAccountValidation(String token,Long accountId, Transaction transaction, AmountService amountService) throws SQLException {
        if (amountService.accountIncome(token,accountId) < transaction.getAmount()){
            throw new ValidationTransactionException("You don't have that amount on your account");
        }
    }
}
