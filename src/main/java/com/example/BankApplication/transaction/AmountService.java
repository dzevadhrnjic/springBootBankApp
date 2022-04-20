package com.example.BankApplication.transaction;

import com.example.BankApplication.account.AccountService;
import com.example.BankApplication.user.TokenUtil;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service

public class AmountService {

    AccountService accountService = new AccountService();
    TokenUtil tokenUtil = new TokenUtil();

    public static final String URL = "jdbc:postgresql://localhost:5432/bank";

    public static final String TABLE_TRANSACTION = "dbtransaction";
    public static final String COLUMN_TRANSACTION_ID = "id";
    public static final String COLUMN_TRANSACTION_SOURCE_ACCOUNT = "sourceaccount";
    public static final String COLUMN_TRANSACTION_DESTINATION_ACCOUNT = "destinationaccount";
    public static final String COLUMN_TRANSACTION_AMOUNT = "amount";
    public static final String COLUMN_TRANSACTION_CREATED_AT = "createdat";
    public static final String COLUMN_TRANSACTION_USER_ID = "userid";
    public static final String COLUMN_BALANCE = "balance";

    public static final int INDEX_AMOUNT = 1;

    public static final String AMOUNT_SOURCE_ACCOUNT = " SELECT SUM(amount) AS balance FROM " + TABLE_TRANSACTION +
                                " WHERE " + COLUMN_TRANSACTION_SOURCE_ACCOUNT + " = ? ";

    public static final String AMOUNT_DESTINATION_ACCOUNT = " SELECT SUM(amount) AS balance FROM " + TABLE_TRANSACTION
                                + " WHERE " + COLUMN_TRANSACTION_DESTINATION_ACCOUNT + " = ? ";
    private Connection connection;
    public PreparedStatement amountSourceAccount;
    public PreparedStatement amountDestinationAccount;

    public boolean open(){
        try{
            connection = DriverManager.getConnection(URL, "postgres", "kovilica1234");
            amountSourceAccount = connection.prepareStatement(AMOUNT_SOURCE_ACCOUNT);
            amountDestinationAccount = connection.prepareStatement(AMOUNT_DESTINATION_ACCOUNT);
            return true;
        }catch (SQLException e){
            System.out.println("Couldn't connect to database");
        }
        return false;
    }

    public void close(){
        try {
            if (amountSourceAccount != null){
                amountSourceAccount.close();
            }
            if (amountDestinationAccount != null){
                amountDestinationAccount.close();
            }
            if (connection != null){
                connection.close();
            }
        }catch (SQLException e){
            System.out.println("Couldn't close connection");
        }
    }

    public Double accountIncome(Long accountId)throws SQLException {

        if (open()) {

            amountSourceAccount.setLong(1, accountId);

            ResultSet results = amountSourceAccount.executeQuery();

            results.next();
                Double sumSource = results.getDouble(1);

                close();
                return sumSource;
            }
            throw new SQLException("Couldn't sum sourceAccount");
        }


    public Double accountOutcome(Long accountId) throws SQLException{


        if (open()){

            amountDestinationAccount.setLong(1, accountId);

            ResultSet results = amountDestinationAccount.executeQuery();

            results.next();

                Double sumDestination = results.getDouble("balance");
                close();
                return sumDestination;
        }
        throw new SQLException("Couldn't sum destinationAccount");
    }

    public Balance balance(String token, Long accountId) throws SQLException{

        accountService.listAccountByUserIdAndId(token, accountId);

        if (open()) {

            Balance balance = new Balance();

            Double sourceAccount = accountIncome(accountId);
            Double destinationAccount = accountOutcome(accountId);

            Double result = sourceAccount - destinationAccount;

            balance.setBalance(result);

            close();
            return balance;
        }throw new SQLException("Couldn't find balance");
    }
}
