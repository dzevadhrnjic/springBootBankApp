package com.example.BankApplication.account;

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
        } else if (account.getName().equals(" ")) {
            throw new ValidationAccountException("Can't be empty space, name field is required");
        } else if (account.getName().length() > 50) {
            throw new ValidationAccountException("You name is too long");
        }
    }

    public static void accountInitialBalanceValidation(Account account) {
        if (account.getInitialbalance() == null) {
            throw new ValidationAccountException("Can't be null");
        } else if (account.getInitialbalance() < 0) {
            throw new ValidationAccountException("Can't be less than zero");
        }
    }
}
