package com.example.BankApplication.transaction.Validation;

import com.example.BankApplication.transaction.Model.Transaction;
import com.example.BankApplication.transaction.Service.AmountService;
import com.example.BankApplication.transaction.Exception.ValidationTransactionException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class TransactionValidationService {

    private static AmountService amountService;

    public TransactionValidationService(AmountService amountService) {
        TransactionValidationService.amountService = amountService;
    }

    public static void transactionFieldsValidation(String token, Transaction transaction, Long accountId) throws SQLException {

        amountValidation(transaction);
    }

    public static void amountValidation(Transaction transaction) {
        if (transaction.getAmount() == null) {
            throw new ValidationTransactionException("Amount can't be null");
        } else if (transaction.getAmount() <= 0) {
            throw new ValidationTransactionException("Amount need to be more than zero");
        }
    }

//    public static void sourceAccountValidation(String token, Long accountId, Transaction transaction) throws SQLException {
//        if (amountService.accountIncome(accountId) < transaction.getAmount()) {
//            throw new ValidationTransactionException("You don't have that amount on your account");
//        }
//    }
}
