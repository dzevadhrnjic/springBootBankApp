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
    public ResponseEntity<Object> listAccountByUserId(@RequestHeader(value = "Authorization") String token) throws SQLException{
        try {
            List<Transaction> listTransaction = transactionService.listTransactionsByUserId(token);
            return ResponseEntity.status(HttpStatus.OK).body(listTransaction);
        }catch (InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (ValidationIdTransaction e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
                                                    @RequestBody Transaction transaction) throws SQLException {
        try {
            Transaction createdTransaction = transactionService.createTransaction(token, transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        }catch (ValidationTransactionException | InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (ValidationIdAccountException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping(path = "{transactionId}/{reverse}")
    public ResponseEntity<Object> reverseTransaction(@PathVariable("transactionId")Long transactionId, Transaction transaction) throws SQLException{
        try{
            Transaction transactionReverse = transactionService.reverseTransaction(transactionId);
            return ResponseEntity.status(HttpStatus.OK).body(transactionReverse);
        }catch (ValidationIdTransaction e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
