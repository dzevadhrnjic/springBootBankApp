package com.example.BankApplication.account;


import com.example.BankApplication.pdfFile.GeneratePdf;
import com.example.BankApplication.transaction.AmountService;
import com.example.BankApplication.transaction.Balance;
import com.example.BankApplication.user.EmailNotVerifiedException;
import com.example.BankApplication.user.ValidationIdException;
import com.itextpdf.text.DocumentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(path = "api/accounts")

public class AccountController {

    private final AccountService accountService;
    private final AmountService amountService;
    private final GeneratePdf generatePdf;

    public AccountController(AccountService accountService, AmountService amountService, GeneratePdf generatePdf) {
        this.accountService = accountService;
        this.amountService = amountService;
        this.generatePdf = generatePdf;
    }

    @GetMapping
    public ResponseEntity<Object> listAccountByUserId(@RequestHeader(value = "Authorization") String token)
                                                        throws SQLException {
        try {
            List<Account> listAccounts = accountService.listAccountsByUserId(token);
            return ResponseEntity.status(HttpStatus.OK).body(listAccounts);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UserIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(path = "{accountId}")
    public ResponseEntity<Object> listAccountsById(@RequestHeader(value = "Authorization") String token,
                                                   @PathVariable("accountId") Long accountId) throws SQLException {
        try {
            Account listAccountId = accountService.listAccountByUserIdAndId(token, accountId);
            return ResponseEntity.status(HttpStatus.OK).body(listAccountId);
        } catch (ValidationIdAccountException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(path = "{accountId}/{balance}")
    public ResponseEntity<Object> balance(@RequestHeader(value = "Authorization") String token,
                                          @PathVariable("accountId") Long accountId) throws SQLException {
        try {
            Balance balanceOfAccounts = amountService.balance(token, accountId);
            return ResponseEntity.status(HttpStatus.OK).body(balanceOfAccounts);
        } catch (ValidationIdAccountException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createAccount(@RequestHeader(value = "Authorization") String token,
                                                    @RequestBody Account account) throws SQLException {
        try {
            Account savedAccount = accountService.createAccount(token,account);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAccount);
        } catch (ValidationAccountException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ValidationIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping(path = "{accountId}")
    public ResponseEntity<Object> deleteAccount(@RequestHeader(value = "Authorization") String token,
                                                @PathVariable("accountId") Long accountId) throws SQLException {
        try {
            accountService.deleteAccount(token, accountId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(accountId);
        } catch (ValidationIdAccountException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping(path = "{accountId}")
    public ResponseEntity<Object> updateAccount(@RequestHeader(value = "Authorization") String token,
                                                @PathVariable("accountId") Long accountId, @RequestBody Account account)
                                                throws SQLException {
        try {
            Account updateAccount = accountService.updateAccount(token, accountId, account);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updateAccount);
        } catch (ValidationAccountException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ValidationIdAccountException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(path = "{accountId}/{statements}")
    public ResponseEntity<Object> createPdfFile(@RequestHeader(value = "Authorization") String  token,
                                                @PathVariable("accountId") Long accountId)
                                                throws MessagingException, IOException {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(generatePdf.statement(token, accountId));
        }catch (EmailNotVerifiedException | DocumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch(ValidationIdAccountException | FileNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
