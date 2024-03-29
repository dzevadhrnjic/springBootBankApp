package com.example.BankApplication.transaction.Service;

import com.example.BankApplication.account.Service.AccountService;
import com.example.BankApplication.account.Exception.ValidationIdAccountException;
import com.example.BankApplication.transaction.Model.Balance;
import com.example.BankApplication.transaction.Database.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AmountService {

    @Autowired
    AccountService accountService;

    @Autowired
    TransactionRepository transactionRepository;

    public Double accountIncome(Long accountId) {

        Double sumOfSource = transactionRepository.sourceAccountBalance(accountId);

        if (sumOfSource == null) {
            throw new ValidationIdAccountException("No source account with that id");
        }

        return sumOfSource;
    }

    public Double accountOutcome(Long accountId) {

        Double sumOfDestination = transactionRepository.destinationAccountBalance(accountId);

        if (sumOfDestination == null) {
            throw new ValidationIdAccountException("No destination account with that id");
        }

        return sumOfDestination;
    }

    public Balance balance(String token, Long accountId) {

        accountService.listAccountByUserIdAndId(token, accountId);

        Balance balance = new Balance();

        Double sourceAccount = accountIncome(accountId);
        Double destinationAccount = accountOutcome(accountId);

        if (destinationAccount == null) {
            destinationAccount = 0.0;
        }

        Double result = sourceAccount - destinationAccount;

        balance.setBalance(result);

        return balance;
    }
}
