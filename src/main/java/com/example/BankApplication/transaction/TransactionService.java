package com.example.BankApplication.transaction;

import com.example.BankApplication.account.AccountService;
import com.example.BankApplication.account.InvalidTokenException;
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

    public static final String URL = "jdbc:postgresql://localhost:5432/bank";

    public static final String TABLE_TRANSACTION = "dbtransaction";
    public static final String COLUMN_TRANSACTION_ID = "id";
    public static final String COLUMN_TRANSACTION_SOURCE_ACCOUNT = "sourceaccount";
    public static final String COLUMN_TRANSACTION_DESTINATION_ACCOUNT = "destinationaccount";
    public static final String COLUMN_TRANSACTION_AMOUNT = "amount";
    public static final String COLUMN_TRANSACTION_CREATED_AT = "createdat";
    public static final String COLUMN_TRANSACTION_USER_ID = "userid";

    public static final int INDEX_TRANSACTION_ID = 1;
    public static final int INDEX_TRANSACTION_SOURCE_ACCOUNT = 2;
    public static final int INDEX_TRANSACTION_DESTINATION_ACCOUNT = 3;
    public static final int INDEX_TRANSACTION_AMOUNT = 4;
    public static final int INDEX_TRANSACTION_CREATED_AT = 5;
    public static final int INDEX_TRANSACTION_USER_ID = 6;

    public static final String TRANSACTION_BY_ID = "SELECT " + COLUMN_TRANSACTION_ID + ", "
            + COLUMN_TRANSACTION_SOURCE_ACCOUNT + ", " + COLUMN_TRANSACTION_DESTINATION_ACCOUNT + ", "
            + COLUMN_TRANSACTION_AMOUNT + ", " + COLUMN_TRANSACTION_CREATED_AT + " FROM " + TABLE_TRANSACTION + " WHERE "
            + COLUMN_TRANSACTION_ID + " = ? ";

    public static final String TRANSACTION_BY_USER_ID = "SELECT " + COLUMN_TRANSACTION_ID + ", " + COLUMN_TRANSACTION_SOURCE_ACCOUNT +
            ", " + COLUMN_TRANSACTION_DESTINATION_ACCOUNT + ", " + COLUMN_TRANSACTION_AMOUNT + ", " + COLUMN_TRANSACTION_CREATED_AT +
            ", " + COLUMN_TRANSACTION_USER_ID + " FROM " + TABLE_TRANSACTION  + " WHERE " + COLUMN_TRANSACTION_USER_ID + " = ? ";

    public static final String CREATE_TRANSACTION = "INSERT INTO " + TABLE_TRANSACTION + '(' + COLUMN_TRANSACTION_SOURCE_ACCOUNT
            + ", " + COLUMN_TRANSACTION_DESTINATION_ACCOUNT + ", " + COLUMN_TRANSACTION_AMOUNT + ", "
            + COLUMN_TRANSACTION_CREATED_AT + ", " + COLUMN_TRANSACTION_USER_ID +  ") VALUES ( ?, ?, ?, ?, ?)";

    public static final String DELETE_TRANSACTION = "DELETE FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_ID
                            + " = ? ";

    public static final String UPDATE_TRANSACTION = "UPDATE " + TABLE_TRANSACTION + " SET " + COLUMN_TRANSACTION_SOURCE_ACCOUNT +
            " =?, " + COLUMN_TRANSACTION_DESTINATION_ACCOUNT + " =?, " + COLUMN_TRANSACTION_AMOUNT + " =?, " +
            COLUMN_TRANSACTION_CREATED_AT + " =? " + " WHERE " + COLUMN_TRANSACTION_ID + " =? ";

    private Connection connection;
    private PreparedStatement transactionById;
    private PreparedStatement transactionByUserId;
    private PreparedStatement createTransaction;
    private PreparedStatement deleteTransaction;
    private PreparedStatement updateTransaction;

    public boolean open(){
        try {
            connection = DriverManager.getConnection(URL, "postgres", "kovilica1234");
            transactionById = connection.prepareStatement(TRANSACTION_BY_ID);
            transactionByUserId = connection.prepareStatement(TRANSACTION_BY_USER_ID);
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
            if (transactionById != null){
                transactionById.close();
            }
            if (transactionByUserId != null){
                transactionByUserId.close();
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

    public List<Transaction> listTransactionsByUserId(String token)throws SQLException{

        Long userId = tokenUtil.verifyJwt(token);

        if (open()){

            transactionByUserId.setLong(1, userId);
            ResultSet results = transactionByUserId.executeQuery();

            if (!results.isBeforeFirst())
                throw new InvalidTokenException("Couldn't find id");

            List<Transaction> transactions = new ArrayList<>();

            while (results.next()){
                Transaction transaction = new Transaction();
                transaction.setId(results.getLong(INDEX_TRANSACTION_ID));
                transaction.setSourceaccount(results.getLong(INDEX_TRANSACTION_SOURCE_ACCOUNT));
                transaction.setDestinationaccount(results.getLong(INDEX_TRANSACTION_DESTINATION_ACCOUNT));
                transaction.setAmount(results.getDouble(INDEX_TRANSACTION_AMOUNT));
                transaction.setCreatedat(results.getDate(INDEX_TRANSACTION_CREATED_AT));
                transaction.setUserid(results.getLong(INDEX_TRANSACTION_USER_ID));
                transactions.add(transaction);
            }
            close();
            return transactions;
        }throw new SQLException("Couldn't list transaction by userId");
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
                transaction.setUserid(results.getLong(INDEX_TRANSACTION_USER_ID));
            }
            close();
            return transaction;
        }throw new SQLException("Couldn't list transaction by id");
    }

    public Transaction createTransaction(String token, Transaction transaction, AmountService amountService, Long accountId) throws SQLException {

        Long userId1 = tokenUtil.verifyJwt(token);
        TransactionValidationService.transactionFieldsValidation( token,transaction,
                                transaction.getSourceaccount(), amountService);
        accountService.listAccountById(token, transaction.getSourceaccount());
        accountService.listAccountId(transaction.getDestinationaccount());


        if (open()) {

                createTransaction.setLong(1, transaction.getSourceaccount());
                createTransaction.setLong(2, transaction.getDestinationaccount());
                createTransaction.setDouble(3, transaction.getAmount());
                LocalDate localDate = LocalDate.now();
                createTransaction.setDate(4, Date.valueOf(localDate));
                createTransaction.setLong(5, userId1);

            int affectedRows = createTransaction.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Created Transaction");
                } else {
                    throw new InvalidTokenException("No user with that id");
                }

                ResultSet generatedKeys = createTransaction.getGeneratedKeys();
                if (generatedKeys.next()) {
                    transaction.setId(generatedKeys.getLong(1));
                    transaction.setSourceaccount(generatedKeys.getLong(2));
                    transaction.setDestinationaccount(generatedKeys.getLong(3));
                    transaction.setAmount(generatedKeys.getDouble(4));
                    transaction.setCreatedat(generatedKeys.getDate(5));
                    transaction.setUserid(generatedKeys.getLong(6));
                } else {
                    throw new InvalidTokenException("Couldn't get the id");
                }
            }
            close();
            return transaction;
    }
}

