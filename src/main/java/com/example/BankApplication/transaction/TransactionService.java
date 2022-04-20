package com.example.BankApplication.transaction;

import com.example.BankApplication.account.AccountService;
import com.example.BankApplication.user.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

@Service

public class TransactionService {

    public static TokenUtil tokenUtil = new TokenUtil();

    private final AccountService accountService;

    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    TransactionRepository transactionRepository;



    public List<Transaction> listTransactionsByUserId(String token) throws SQLException {

        Long userId = tokenUtil.verifyJwt(token);

        List<Transaction> transaction = transactionRepository.getTransactionByUserId(userId);

        if (transaction == null) {
            throw new TransactionIdValidation("Couldn't find id");
        }

        return transaction;

    }


    public Transaction listTransactionById(Long transactionId) throws SQLException {

        Transaction transaction = transactionRepository.getTransactionById(transactionId);

        if (transaction == null) {
            throw new ValidationIdTransaction("Couldn't find transaction with that id");
        }

        return transaction;

    }


    public Transaction createTransaction(String token, Transaction transaction) throws SQLException {

        Long userId = tokenUtil.verifyJwt(token);
        TransactionValidationService.transactionFieldsValidation(token, transaction,
                transaction.getSourceaccount());
        accountService.listAccountByUserIdAndId(token, transaction.getSourceaccount());
        accountService.listAccountId(transaction.getDestinationaccount());


        LocalDate localDate = LocalDate.now();
        transaction.setCreatedat(Date.valueOf(localDate));
        transaction.setUserid(userId);

        transactionRepository.save(transaction);

        return transaction;

    }

    public Transaction reverseTransaction(Long transactionId) throws SQLException {

        Transaction transaction = listTransactionById(transactionId);

        Transaction reverse = new Transaction();

        reverse.setSourceaccount(transaction.getDestinationaccount());
        reverse.setDestinationaccount(transaction.getSourceaccount());
        reverse.setAmount(transaction.getAmount());
        LocalDate localDate = LocalDate.now();
        reverse.setCreatedat(Date.valueOf(localDate));
        reverse.setUserid(transaction.getUserid());

        transactionRepository.save(reverse);

        return reverse;

    }
}

