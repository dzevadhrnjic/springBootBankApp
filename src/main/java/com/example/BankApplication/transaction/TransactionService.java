package com.example.BankApplication.transaction;

import com.example.BankApplication.account.AccountService;
import com.example.BankApplication.account.ValidationIdAccountException;
import com.example.BankApplication.user.TokenUtil;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service

public class TransactionService {

    TokenUtil tokenUtil = new TokenUtil();

    public static AccountService accountService = new AccountService();
    public static AmountService amountService = new AmountService();

    public static final String URL = "jdbc:postgresql://localhost:5432/bank";

    public static final String TABLE_TRANSACTION = "dbtransaction";
    public static final String COLUMN_TRANSACTION_ID = "id";
    public static final String COLUMN_TRANSACTION_SOURCE_ACCOUNT = "sourceaccount";
    public static final String COLUMN_TRANSACTION_DESTINATION_ACCOUNT = "destinationaccount";
    public static final String COLUMN_TRANSACTION_AMOUNT = "amount";
    public static final String COLUMN_TRANSACTION_CREATED_AT = "createdat";

    public static final int INDEX_TRANSACTION_ID = 1;
    public static final int INDEX_TRANSACTION_SOURCE_ACCOUNT = 2;
    public static final int INDEX_TRANSACTION_DESTINATION_ACCOUNT = 3;
    public static final int INDEX_TRANSACTION_AMOUNT = 4;
    public static final int INDEX_TRANSACTION_CREATED_AT = 5;

//    public static final String TRANSACTIONS = "SELECT * FROM " + TABLE_TRANSACTION;

    public static final String TRANSACTIONS = "SELECT " + COLUMN_TRANSACTION_ID + ", "
            + COLUMN_TRANSACTION_SOURCE_ACCOUNT + ", " + COLUMN_TRANSACTION_DESTINATION_ACCOUNT + ", "
            + COLUMN_TRANSACTION_AMOUNT + ", " + COLUMN_TRANSACTION_CREATED_AT + " FROM " + TABLE_TRANSACTION + " WHERE "
            + COLUMN_TRANSACTION_ID + " = ? ";

    public static final String TRANSACTION_BY_ID = "SELECT " + COLUMN_TRANSACTION_ID + ", "
            + COLUMN_TRANSACTION_SOURCE_ACCOUNT + ", " + COLUMN_TRANSACTION_DESTINATION_ACCOUNT + ", "
            + COLUMN_TRANSACTION_AMOUNT + ", " + COLUMN_TRANSACTION_CREATED_AT + " FROM " + TABLE_TRANSACTION + " WHERE "
            + COLUMN_TRANSACTION_ID + " = ? ";

    public static final String CREATE_TRANSACTION = "INSERT INTO " + TABLE_TRANSACTION + '(' + COLUMN_TRANSACTION_SOURCE_ACCOUNT
            + ", " + COLUMN_TRANSACTION_DESTINATION_ACCOUNT + ", " + COLUMN_TRANSACTION_AMOUNT + ", "
            + COLUMN_TRANSACTION_CREATED_AT + ") VALUES ( ?, ?, ?, ?)";

    public static final String DELETE_TRANSACTION = "DELETE FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_ID
                            + " = ? ";

    public static final String UPDATE_TRANSACTION = "UPDATE " + TABLE_TRANSACTION + " SET " + COLUMN_TRANSACTION_SOURCE_ACCOUNT +
            " =?, " + COLUMN_TRANSACTION_DESTINATION_ACCOUNT + " =?, " + COLUMN_TRANSACTION_AMOUNT + " =?, " +
            COLUMN_TRANSACTION_CREATED_AT + " =? " + " WHERE " + COLUMN_TRANSACTION_ID + " =? ";

    private Connection connection;
    private PreparedStatement transactions;
    private PreparedStatement transactionById;
    private PreparedStatement createTransaction;
    private PreparedStatement deleteTransaction;
    private PreparedStatement updateTransaction;

    public boolean open(){
        try {
            connection = DriverManager.getConnection(URL, "postgres", "kovilica1234");
            transactions = connection.prepareStatement(TRANSACTIONS);
            transactionById = connection.prepareStatement(TRANSACTION_BY_ID);
            createTransaction = connection.prepareStatement(CREATE_TRANSACTION, Statement.RETURN_GENERATED_KEYS);
            deleteTransaction = connection.prepareStatement(DELETE_TRANSACTION);
            updateTransaction = connection.prepareStatement(UPDATE_TRANSACTION);
            return true;
        }catch (SQLException e){
            System.out.println("Couldn't connect to database");
        }
        return false;
    }

    public void close(){
        try {
            if (transactions != null){
                transactions.close();
            }
            if (transactionById != null){
                transactionById.close();
            }
            if (createTransaction != null){
                createTransaction.close();
            }
            if (deleteTransaction != null){
                deleteTransaction.close();
            }
            if (updateTransaction != null){
                updateTransaction.close();
            }
            if (connection != null){
                connection.close();
            }
        }catch (SQLException e){
            System.out.println("Couldn't close connection");
        }
    }

    public List<Transaction> listTransactions(String token) throws SQLException{

        Long transactionId = tokenUtil.verifyJwt(token);

        if (open()) {
//
//            StringBuilder stringBuilder = new StringBuilder("SELECT * FROM ");
//            stringBuilder.append(TABLE_TRANSACTION);
//
//            try (Statement statement = connection.createStatement();
//                 ResultSet results = statement.executeQuery(stringBuilder.toString())) {

                transactions.setLong(1, transactionId);
                ResultSet results = transactions.executeQuery();

                if (!results.next())
                    throw new TransactionIdValidation("No transaction with that id");

                List<Transaction> transactions = new ArrayList<>();

                while (results.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setId(results.getLong(INDEX_TRANSACTION_ID));
                    transaction.setSourceaccount(results.getLong(INDEX_TRANSACTION_SOURCE_ACCOUNT));
                    transaction.setDestinationaccount(results.getLong(INDEX_TRANSACTION_DESTINATION_ACCOUNT));
                    transaction.setAmount(results.getDouble(INDEX_TRANSACTION_AMOUNT));
                    transaction.setCreatedat(results.getDate(INDEX_TRANSACTION_CREATED_AT));
                    transactions.add(transaction);
                }
                close();
                return transactions;
        }
        throw new ValidationIdAccountException("Couldn't find id");
    }

    public Transaction listTransactionById(Long transactionId) throws SQLException{

        if (open()){
            transactionById.setLong(1, transactionId);
            ResultSet results = transactionById.executeQuery();

            if (!results.isBeforeFirst()){
                throw new ValidationIdTransaction("Couldn't find transaction with that id");
            }

            Transaction transaction = new Transaction();
            while (results.next()){
                transaction.setId(results.getLong(INDEX_TRANSACTION_ID));
                transaction.setSourceaccount(results.getLong(INDEX_TRANSACTION_SOURCE_ACCOUNT));
                transaction.setDestinationaccount(results.getLong(INDEX_TRANSACTION_DESTINATION_ACCOUNT));
                transaction.setAmount(results.getDouble(INDEX_TRANSACTION_AMOUNT));
                transaction.setCreatedat(results.getDate(INDEX_TRANSACTION_CREATED_AT));
            }
            close();
            return transaction;
        }throw new SQLException("Couldn't list transaction by id");
    }

    public Transaction createTransaction(Transaction transaction, AmountService amountService, Long accountId)throws SQLException{

        TransactionValidationService.transactionFieldsValidation(transaction, transaction.getSourceaccount(), amountService);
        accountService.listAccountById(transaction.getSourceaccount());
        accountService.listAccountById(transaction.getDestinationaccount());

        if (open()){

            createTransaction.setLong(1, transaction.getSourceaccount());
            createTransaction.setLong(2, transaction.getDestinationaccount());
            createTransaction.setDouble(3, transaction.getAmount());
            LocalDate localDate = LocalDate.now();
            createTransaction.setDate(4, Date.valueOf(localDate));

            int affectedRows = createTransaction.executeUpdate();

            if (affectedRows != 1){
                System.out.println("Couldn't create transaction");
            }

            ResultSet generatedKeys = createTransaction.getGeneratedKeys();
            if (generatedKeys.next()){
                transaction.setId(generatedKeys.getLong(1));
                transaction.setSourceaccount(generatedKeys.getLong(2));
                transaction.setDestinationaccount(generatedKeys.getLong(3));
                transaction.setAmount(generatedKeys.getDouble(4));
                transaction.setCreatedat(generatedKeys.getDate(5));
            }else {
                throw new SQLException("Couldn't get the id");
            }
        }
        close();
        return transaction;
    }

    public Transaction deleteTransaction(Long transactionId)throws SQLException{

        listTransactionById(transactionId);

        if (open()) {

            deleteTransaction.setLong(1, transactionId);

            int affectedRows = deleteTransaction.executeUpdate();

            if (affectedRows != 1) {
                System.out.println("Couldn't delete transaction");
            }
        }else {
                throw new SQLException("Couldn't get the id");
            }
        close();
        return null;
    }

    public Transaction updateTransaction(Long transactionId, Transaction transaction, Long accountId, AmountService amountService) throws SQLException{

        accountService.listAccountById(transaction.getDestinationaccount());
        accountService.listAccountById(transaction.getSourceaccount());
        TransactionValidationService.transactionFieldsValidation(transaction, transaction.getSourceaccount(), amountService);
        listTransactionById(transactionId);

        if (open()){

            updateTransaction.setLong(1, transaction.getSourceaccount());
            updateTransaction.setLong(2, transaction.getDestinationaccount());
            updateTransaction.setDouble(3, transaction.getAmount());
            LocalDate localDate = LocalDate.now();
            updateTransaction.setDate(4, Date.valueOf(localDate));
            updateTransaction.setLong(5, transactionId);

            int affectedRows = updateTransaction.executeUpdate();

            if (affectedRows > 0){
                System.out.println("Transaction was updated");
            }else {
                throw new SQLException("Couldn't get the id");
            }
        }
        close();
        return transaction;
    }
}

