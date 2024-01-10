package com.example.BankApplication.account.Controller;


import com.example.BankApplication.account.Exception.InvalidTokenException;
import com.example.BankApplication.account.Model.Account;
import com.example.BankApplication.account.Service.AccountService;
import com.example.BankApplication.account.Exception.UserIdException;
import com.example.BankApplication.account.Exception.ValidationAccountException;
import com.example.BankApplication.account.Exception.ValidationIdAccountException;
import com.example.BankApplication.blacklist.exception.BlackListTokenException;
import com.example.BankApplication.pdfFile.Service.GeneratePdf;
import com.example.BankApplication.transaction.Service.AmountService;
import com.example.BankApplication.transaction.Model.Balance;
import com.example.BankApplication.user.Exception.EmailNotVerifiedException;
import com.example.BankApplication.user.Exception.ValidationIdException;
import com.itextpdf.text.DocumentException;
import io.jsonwebtoken.impl.crypto.RsaSignatureValidator;
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


    @GetMapping("all")
    public ResponseEntity<Object> listAccounts(@RequestParam(name = "pageNumber")int pageNumber,
                                               @RequestParam(name = "pageSize") int pageSize,
                                               @RequestParam(name = "name", required = false) String name){

        try {
            List<Account> listAccounts = accountService.listAccounts(pageNumber, pageSize, name);
            return ResponseEntity.status(HttpStatus.OK).body(listAccounts);
        }catch (ValidationIdAccountException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

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
        } catch (BlackListTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping(path = "{accountId}")
    public ResponseEntity<Object> listAccountsById(@RequestHeader(value = "Authorization") String token,
                                                   @PathVariable("accountId") Long accountId) {
        try {
            Account listAccountId = accountService.listAccountByUserIdAndId(token, accountId);
            return ResponseEntity.status(HttpStatus.OK).body(listAccountId);
        } catch (ValidationIdAccountException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BlackListTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping(path = "{accountId}/balance")
    public ResponseEntity<Object> balance(@RequestHeader(value = "Authorization") String token,
                                          @PathVariable("accountId") Long accountId) {
        try {
            Balance balanceOfAccounts = amountService.balance(token, accountId);
            return ResponseEntity.status(HttpStatus.OK).body(balanceOfAccounts);
        } catch (ValidationIdAccountException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BlackListTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
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
        }catch (BlackListTokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @DeleteMapping(path = "{accountId}")
    public ResponseEntity<Object> deleteAccount(@RequestHeader(value = "Authorization") String token,
                                                @PathVariable("accountId") Long accountId) {
        try {
            accountService.deleteAccount(token, accountId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(accountId);
        } catch (ValidationIdAccountException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (BlackListTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
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
        } catch (BlackListTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping(path = "createPdf/{accountId}/statements")
    public ResponseEntity<Object> createPdfFile(@RequestHeader(value = "Authorization") String  token,
                                                @PathVariable("accountId") Long accountId)
                                                throws MessagingException, IOException {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(generatePdf.statement(token, accountId));
        } catch (EmailNotVerifiedException | DocumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(ValidationIdAccountException | FileNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(BlackListTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
