package com.example.BankApplication.transaction.Model;

import javax.persistence.Table;

@Table(name = "dbtransaction")

public class Balance {

    private Double balance;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

}
