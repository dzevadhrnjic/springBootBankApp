package com.example.BankApplication.transaction.Controller;

import com.example.BankApplication.account.Exception.InvalidTokenException;
import com.example.BankApplication.account.Exception.ValidationIdAccountException;
import com.example.BankApplication.blacklist.exception.BlackListTokenException;
import com.example.BankApplication.transaction.Model.Transaction;
import com.example.BankApplication.transaction.Service.TransactionService;
import com.example.BankApplication.transaction.Exception.ValidationIdTransaction;
import com.example.BankApplication.transaction.Exception.ValidationTransactionException;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(path = "allTransactions")
    public ResponseEntity<Object> listAllTransaction(@RequestParam(name = "pageNumber") int pageNumber,
                                                     @RequestParam(name = "pageSize") int pageSize,
                                                     @RequestParam(name = "order", required = false) String order,
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                     @RequestParam(name = "dateFrom", required = false) Date dateFrom,
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                     @RequestParam(name = "dateTo", required = false) Date dateTo) {

        try {
            List<Transaction> listTransactions = transactionService.listAllTransactions(pageNumber, pageSize, order, dateFrom, dateTo);
            return ResponseEntity.status(HttpStatus.OK).body(listTransactions);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(path = "userTransaction")
    public ResponseEntity<Object> listTransactionsByUserId(@RequestHeader(value = "Authorization") String token) throws SQLException {
        try {
            List<Transaction> listTransaction = transactionService.listTransactionsByUserId(token);
            return ResponseEntity.status(HttpStatus.OK).body(listTransaction);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ValidationIdTransaction e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BlackListTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping(path = "{user}/{transactionId}")
    public ResponseEntity<Object> listTransactionById(@PathVariable("transactionId") Long transactionId) throws SQLException {
        try {
            Transaction listTransactionId = transactionService.listTransactionById(transactionId);
            return ResponseEntity.status(HttpStatus.OK).body(listTransactionId);
        } catch (ValidationIdTransaction e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createTransaction(@RequestHeader(value = "Authorization") String token,
                                                    @RequestBody Transaction transaction) throws SQLException {
        try {
            Transaction createdTransaction = transactionService.createTransaction(token, transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        } catch (ValidationTransactionException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ValidationIdAccountException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BlackListTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping(path = "{transactionId}/{reverse}")
    public ResponseEntity<Object> reverseTransaction(@PathVariable("transactionId") Long transactionId) throws SQLException {
        try {
            Transaction transactionReverse = transactionService.reverseTransaction(transactionId);
            return ResponseEntity.status(HttpStatus.OK).body(transactionReverse);
        } catch (ValidationIdTransaction e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
