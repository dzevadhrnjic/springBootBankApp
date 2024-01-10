package com.example.BankApplication.account.Validation;

import com.example.BankApplication.account.Model.Account;
import com.example.BankApplication.account.Exception.ValidationAccountException;

import java.sql.SQLException;


public class AccountValidationService {

    public static void accountFieldsValidation(Account account) throws SQLException {

        accountNameValidation(account);
        accountInitialBalanceValidation(account);
    }

    public static void accountNameValidation(Account account) {
        if (account.getName() == null) {
            throw new ValidationAccountException("Name field required");
        } else if (account.getName().equals("")) {
            throw new ValidationAccountException("Can't be empty, name field is required");
        } else if (account.getName().length() > 50) {
            throw new ValidationAccountException("Your name is too long");
        }
    }

    public static void accountInitialBalanceValidation(Account account) {
        if (account.getInitialBalance() == null) {
            throw new ValidationAccountException("Can't be null");
        } else if (account.getInitialBalance() < 0) {
            throw new ValidationAccountException("Can't be less than zero");
        }
    }
}
