package com.example.BankApplication.transaction;

import com.example.BankApplication.account.InvalidTokenException;
import com.example.BankApplication.account.ValidationIdAccountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(path = "api/transactions")

public class TransactionController {

    TransactionService transactionService;
    AmountService amountService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<Object> listTransactions() throws SQLException {
        try {
            List<Transaction> listTransactionsById = transactionService.listTransactions();
            return ResponseEntity.status(HttpStatus.OK).body(listTransactionsById);
        }catch (InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (TransactionIdValidation e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(path = "{transactionId}")
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
                                                    @RequestHeader(value = "Authorization1") String token1,
                                                    @RequestBody Transaction transaction,
                                                    AmountService amountService) throws SQLException {
        try {
            Transaction createdTransaction = transactionService.createTransaction(token, token1,transaction,
                                                amountService);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        }catch (ValidationTransactionException | InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (ValidationIdAccountException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
