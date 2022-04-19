package com.example.BankApplication.account;

import com.example.BankApplication.user.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service

public class AccountService {

    TokenUtil tokenUtil = new TokenUtil();

    @Autowired
    AccountRepository accountRepository;
    
    public List<Account> listAccountsByUserId(String token){

        Long userId = tokenUtil.verifyJwt(token);

        List<Account> account = accountRepository.getAccountByUserId(userId);

        if (account == null){
            throw new ValidationIdAccountException("No account for that userId");
        }

        return account;

    }

        public Account listAccountById(String token, Long accountId) {

                Long userId = tokenUtil.verifyJwt(token);
                Account account = accountRepository.getAccountById(accountId, userId);

                if (account == null){
                    throw new ValidationIdAccountException("No account with that id");
                }

                return account;

        }

        public Account listAccountId(Long accountId) {

        Account account = accountRepository.getAccountId(accountId);

        if (account == null){
            throw new ValidationIdAccountException("No account with that id");
        }

             return account;

        }

    public Account createAccount(Account account) throws SQLException {

        AccountValidationService.accountFieldsValidation(account);

        LocalDate localDate = LocalDate.now();
        account.setCreatedat(Date.valueOf(localDate));

        accountRepository.save(account);

        return account;
    }

    @Transactional
    public void deleteAccount(String token, Long accountId){

        listAccountById(token,accountId);
        Long idAccount = tokenUtil.verifyJwt(token);

        accountRepository.deleteAccount(accountId,idAccount);

    }
    public Account updateAccount(String token, Long accountId, Account account) throws SQLException{

        Long userId = tokenUtil.verifyJwt(token);
        listAccountById(token,accountId);
        AccountValidationService.accountFieldsValidation(account);

        LocalDate localDate = LocalDate.now();
        account.setCreatedat(Date.valueOf(localDate));
        account.setId(accountId);
        account.setUserid(userId);

        accountRepository.save(account);

        return account;

    }
}
