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
//        Transaction transaction = listTransactionById(transactionId);
//
//        if (open()){
//
//            createTransaction.setLong(1, transaction.getId());
//            createTransaction.setLong(1, transaction.getSourceaccount());
//            createTransaction.setLong(2, transaction.getDestinationaccount());
//            createTransaction.setDouble(3, transaction.getAmount());
//            LocalDate localDate = LocalDate.now();
//            createTransaction.setDate(4, Date.valueOf(localDate));
//            createTransaction.setLong(5, transaction.getUserid());
//
//            int affectedRows = createTransaction.executeUpdate();
//
//            if (affectedRows > 0) {
//                System.out.println("Created Transaction");
//            } else {
//                throw new InvalidTokenException("No user with that id");
//            }
//
//            ResultSet generatedKeys = createTransaction.getGeneratedKeys();
//            if (generatedKeys.next()) {
//                transaction.setId(generatedKeys.getLong(1));
//                transaction.setSourceaccount(generatedKeys.getLong(2));
//                transaction.setDestinationaccount(generatedKeys.getLong(3));
//                transaction.setAmount(generatedKeys.getDouble(4));
//                transaction.setCreatedat(generatedKeys.getDate(5));
//                transaction.setUserid(generatedKeys.getLong(6));
//            } else {
//                throw new InvalidTokenException("Couldn't get the id");
//            }
//        }
//        close();
//        return transaction;
//    }
    }
}

