package com.example.BankApplication.account.Service;

import com.example.BankApplication.account.Database.AccountSpecification;
import com.example.BankApplication.account.Model.Account;
import com.example.BankApplication.account.Database.AccountRepository;
import com.example.BankApplication.account.Validation.AccountValidationService;
import com.example.BankApplication.account.Exception.ValidationIdAccountException;
import com.example.BankApplication.user.Util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    AccountRepository accountRepository;

    public List<Account> listAccounts(int pageNumber, int pageSize, String name) {

        Pageable paging = PageRequest.of(pageNumber, pageSize);

        Specification<Account> specification = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            specification = specification.and(AccountSpecification.hasName(name));
        }

        return accountRepository.findAll(specification, paging);
    }

    public List<Account> listAccountsByUserId(String token) {

        Long userId = tokenUtil.verifyJwt(token);

        List<Account> account = accountRepository.getAccountByUserId(userId);

        if (account == null) {
            throw new ValidationIdAccountException("No account for that userId");
        }

        return account;
    }

    public Account listAccountByUserIdAndId(String token, Long accountId) {

        Long userId = tokenUtil.verifyJwt(token);
        Account account = accountRepository.getAccountByUserIdAndUserId(userId, accountId);

        if (account == null) {
            throw new ValidationIdAccountException("No account with that id");
        }

        return account;
    }

    public Account listAccountId(Long accountId) {

        Account account = accountRepository.getAccountId(accountId);

        if (account == null) {
            throw new ValidationIdAccountException("No account with that id");
        }

        return account;
    }

    public Account createAccount(String token, Account account) throws SQLException {

        // blacklistService.blackListOfTokens(token);
        Long userId = tokenUtil.verifyJwt(token);
        AccountValidationService.accountFieldsValidation(account);

        LocalDateTime localDateAndTime = LocalDateTime.now();
        account.setCreatedAt(localDateAndTime);
        account.setUserid(userId);

        accountRepository.save(account);

        return account;
    }

    @Transactional
    public void deleteAccount(String token, Long accountId) {

        listAccountByUserIdAndId(token, accountId);
        Long idAccount = tokenUtil.verifyJwt(token);

        accountRepository.deleteAccount(accountId, idAccount);
    }

    public Account updateAccount(String token, Long accountId, Account account) throws SQLException {

        Long userId = tokenUtil.verifyJwt(token);
        listAccountByUserIdAndId(token, accountId);
        AccountValidationService.accountFieldsValidation(account);

        LocalDateTime localDateAndTime = LocalDateTime.now();
        account.setCreatedAt(localDateAndTime);
        account.setId(accountId);
        account.setUserid(userId);

        accountRepository.save(account);

        return account;
    }
}
