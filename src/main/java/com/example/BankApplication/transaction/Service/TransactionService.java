package com.example.BankApplication.transaction.Service;

import com.example.BankApplication.account.Model.Account;
import com.example.BankApplication.account.Service.AccountService;
import com.example.BankApplication.account.Exception.InvalidTokenException;
import com.example.BankApplication.transaction.Database.TransactionRepository;
import com.example.BankApplication.transaction.Database.TransactionSpecification;
import com.example.BankApplication.transaction.Exception.TransactionIdValidation;
import com.example.BankApplication.transaction.Exception.ValidationIdTransaction;
import com.example.BankApplication.transaction.Model.Transaction;
import com.example.BankApplication.transaction.Validation.TransactionValidationService;
import com.example.BankApplication.user.Util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    private AccountService accountService;


    @Autowired
    TransactionRepository transactionRepository;

    public List<Transaction> listAllTransactions(int pageNumber, int pageSize, String order, Date dateFrom, Date dateTo){

        Pageable paging = PageRequest.of(pageNumber, pageSize);

        Specification<Transaction> specification = Specification.where(null);

        if (!order.isEmpty() && dateFrom != null && dateTo != null) {
            specification = specification.and(TransactionSpecification.transactionByDateAsc(order, dateFrom, dateTo));
        } else if (order.equalsIgnoreCase("asc") || order.equalsIgnoreCase("desc")) {
            specification = specification.and(TransactionSpecification.orderByAscOrByDescCreatedat(order));
        }

        return transactionRepository.findAll(specification, paging);
    }

    public List<Transaction> listTransactionsByUserId(String token) throws SQLException {

        Long userId = tokenUtil.verifyJwt(token);

        List<Transaction> transaction = transactionRepository.getTransactionByUserId(userId);

        if (transaction == null) {
            throw new TransactionIdValidation("Couldn't find transaction for that user");
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

        if (userId == null){
            throw new InvalidTokenException("Couldn't create transaction, unauthorized user");
        }

        LocalDateTime localDateAndTime = LocalDateTime.now();
        transaction.setCreatedat(localDateAndTime);
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
        LocalDateTime localDateAndTime = LocalDateTime.now();
        transaction.setCreatedat(localDateAndTime);
        reverse.setUserid(transaction.getUserid());

        transactionRepository.save(reverse);

        return reverse;
    }
}

