package com.example.BankApplication.account;

import com.example.BankApplication.user.TokenUtil;
import com.example.BankApplication.user.UserService;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service

public class AccountService {

    UserService userService = new UserService();
    TokenUtil tokenUtil = new TokenUtil();

    public static final String URL = "jdbc:postgresql://localhost:5432/bank";

    public static final String TABLE_ACCOUNT = "account";
    public static final String COLUMN_ACCOUNT_ID = "id";
    public static final String COLUMN_ACCOUNT_NAME = "name";
    public static final String COLUMN_ACCOUNT_INITIAL_BALANCE = "initialbalance";
    public static final String COLUMN_ACCOUNT_USER_ID = "userid";
    public static final String COLUMN_ACCOUNT_CREATED_AT = "createdat";

    public static final int INDEX_ACCOUNT_ID = 1;
    public static final int INDEX_ACCOUNT_NAME = 2;
    public static final int INDEX_ACCOUNT_INITIAL_BALANCE = 3;
    public static final int INDEX_ACCOUNT_USER_ID = 4;
    public static final int INDEX_CREATED_AT = 5;

    public static final String ACCOUNTS = "SELECT " + COLUMN_ACCOUNT_ID + " FROM " + TABLE_ACCOUNT +
            " WHERE " + COLUMN_ACCOUNT_ID + " = ? ";

    public static final String ACCOUNT_BY_ID = "SELECT " + COLUMN_ACCOUNT_ID + ", " + COLUMN_ACCOUNT_NAME + ", " +
            COLUMN_ACCOUNT_INITIAL_BALANCE + ", " + COLUMN_ACCOUNT_USER_ID + ", " + COLUMN_ACCOUNT_CREATED_AT +
            " FROM " + TABLE_ACCOUNT + " WHERE " + COLUMN_ACCOUNT_USER_ID + " = ? AND " + COLUMN_ACCOUNT_ID + " = ? ";

    public static final String ACCOUNTS_BY_USER_ID = "SELECT " + COLUMN_ACCOUNT_ID + ", " + COLUMN_ACCOUNT_NAME +
            " , " + COLUMN_ACCOUNT_INITIAL_BALANCE + ", " + COLUMN_ACCOUNT_USER_ID + ", " +
            COLUMN_ACCOUNT_CREATED_AT + " FROM " + TABLE_ACCOUNT + " WHERE " + COLUMN_ACCOUNT_USER_ID +
            " = ? ";

    public static final String ACCOUNT_ID = " SELECT " + COLUMN_ACCOUNT_ID + ", " + COLUMN_ACCOUNT_NAME +
            " , " + COLUMN_ACCOUNT_INITIAL_BALANCE + ", " + COLUMN_ACCOUNT_USER_ID + ", " +
            COLUMN_ACCOUNT_CREATED_AT + " FROM " + TABLE_ACCOUNT + " WHERE " + COLUMN_ACCOUNT_ID + " = ? ";

    public static final String CREATE_ACCOUNT = "INSERT INTO " + TABLE_ACCOUNT + '(' + COLUMN_ACCOUNT_NAME + ", " +
            COLUMN_ACCOUNT_INITIAL_BALANCE + ", " + COLUMN_ACCOUNT_USER_ID + ", " + COLUMN_ACCOUNT_CREATED_AT + ") VALUES (?, ?, ?, ?)";

    public static final String DELETE_ACCOUNT = "DELETE FROM " + TABLE_ACCOUNT + " WHERE " + COLUMN_ACCOUNT_USER_ID + " = ? AND " +
            COLUMN_ACCOUNT_ID + " = ? ";

    public static final String UPDATE_ACCOUNT = "UPDATE " + TABLE_ACCOUNT + " SET " + COLUMN_ACCOUNT_NAME + " =?, " +
            COLUMN_ACCOUNT_INITIAL_BALANCE + " =?, " + COLUMN_ACCOUNT_USER_ID + " =?, " + COLUMN_ACCOUNT_CREATED_AT +
            " =? " + " WHERE " + COLUMN_ACCOUNT_ID + " = ? AND " + COLUMN_ACCOUNT_USER_ID + " = ? ";

    private Connection connection;

    private PreparedStatement accounts;
    private PreparedStatement accountByUserId;
    private PreparedStatement accountById;
    private PreparedStatement idAccount;
    private PreparedStatement createAccount;
    private PreparedStatement deleteAccount;
    private PreparedStatement updateAccount;

    public boolean open() {
        try {
            connection = DriverManager.getConnection(URL, "postgres", "kovilica1234");
            accounts = connection.prepareStatement(ACCOUNTS);
            accountByUserId = connection.prepareStatement(ACCOUNTS_BY_USER_ID);
            accountById = connection.prepareStatement(ACCOUNT_BY_ID);
            idAccount = connection.prepareStatement(ACCOUNT_ID);
            createAccount = connection.prepareStatement(CREATE_ACCOUNT, Statement.RETURN_GENERATED_KEYS);
            deleteAccount = connection.prepareStatement(DELETE_ACCOUNT);
            updateAccount = connection.prepareStatement(UPDATE_ACCOUNT);
            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database" + e);
            return false;
        }
    }

    public void close() {
        try {
            if (accounts != null) {
                accounts.close();
            }
            if (accountByUserId != null){
                accountByUserId.close();
            }
            if (accountById != null) {
                accountById.close();
            }
            if (idAccount != null){
                idAccount.close();
            }
            if (createAccount != null) {
                createAccount.close();
            }
            if (deleteAccount != null) {
                deleteAccount.close();
            }
            if (updateAccount != null) {
                updateAccount.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't close connection");
        }
    }

    public List<Account> listAccounts() throws SQLException {

        if (open()) {

            StringBuilder stringBuilder = new StringBuilder("SELECT * FROM ");
            stringBuilder.append(TABLE_ACCOUNT);

            try (Statement statement = connection.createStatement();
                 ResultSet results = statement.executeQuery(stringBuilder.toString())) {

                List<Account> accounts = new ArrayList<>();

                while (results.next()) {

                    Account account = new Account();
                    account.setId(results.getLong(INDEX_ACCOUNT_ID));
                    account.setName(results.getString(INDEX_ACCOUNT_NAME));
                    account.setInitialbalance(results.getDouble(INDEX_ACCOUNT_INITIAL_BALANCE));
                    account.setUserid(results.getLong(INDEX_ACCOUNT_USER_ID));
                    account.setCreatedat(results.getDate(INDEX_CREATED_AT));
                    accounts.add(account);
                }
                close();
                return accounts;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Account> listAccountsByUserId(String token)throws SQLException {

        Long userId = tokenUtil.verifyJwt(token);

        if (open()) {

            accountByUserId.setLong(1, userId);
            ResultSet results = accountByUserId.executeQuery();

            if (!results.isBeforeFirst())
                throw new UserIdException("No account with that id");

            List<Account> accounts = new ArrayList<>();

            while (results.next()) {
                Account account = new Account();
                account.setId(results.getLong(INDEX_ACCOUNT_ID));
                account.setName(results.getString(INDEX_ACCOUNT_NAME));
                account.setInitialbalance(results.getDouble(INDEX_ACCOUNT_INITIAL_BALANCE));
                account.setUserid(results.getLong(INDEX_ACCOUNT_USER_ID));
                account.setCreatedat(results.getDate(INDEX_CREATED_AT));
                accounts.add(account);
            }
            close();
            return accounts;
        }
        throw new ValidationIdAccountException("Can't find the id");
    }

        public Account listAccountById(String token, Long accountId) throws SQLException {

                Long userId = tokenUtil.verifyJwt(token);

                if (open()) {

                    accountById.setLong(1,userId);
                    accountById.setLong(2, accountId);

                    ResultSet results = accountById.executeQuery();

                    if (!results.isBeforeFirst())
                    throw new ValidationIdAccountException("Couldn't find the id");

                    Account account = new Account();

                    while (results.next()) {
                        account.setId(results.getLong(INDEX_ACCOUNT_ID));
                        account.setName(results.getString(INDEX_ACCOUNT_NAME));
                        account.setInitialbalance(results.getDouble(INDEX_ACCOUNT_INITIAL_BALANCE));
                        account.setUserid(results.getLong(INDEX_ACCOUNT_USER_ID));
                        account.setCreatedat(results.getDate(INDEX_CREATED_AT));
                }
                close();
                return account;

            }
            throw new SQLException("Couldn't list account by Id");
        }

        public Account listAccountId(Long accountId) throws SQLException{

        if (open()){

                idAccount.setLong(1, accountId);

                ResultSet results = idAccount.executeQuery();

                if (!results.isBeforeFirst())
                    throw new ValidationIdAccountException("Couldn't find the id");

                Account account = new Account();

                while (results.next()){
                    account.setId(results.getLong(INDEX_ACCOUNT_ID));
                    account.setName(results.getString(INDEX_ACCOUNT_NAME));
                    account.setInitialbalance(results.getDouble(INDEX_ACCOUNT_INITIAL_BALANCE));
                    account.setCreatedat(results.getDate(INDEX_CREATED_AT));
                }

                close();
                return account;
            }
        throw new SQLException("Couldn't list account");
        }


    public Account createAccount(Account account) throws SQLException {

            AccountValidationService.accountFieldsValidation(account);
            userService.listUserById(account.getUserid());

            if (open()) {

                createAccount.setString(1, account.getName());
                createAccount.setDouble(2, account.getInitialbalance());
                createAccount.setLong(3, account.getUserid());
                LocalDate localDate = LocalDate.now();
                createAccount.setDate(4, Date.valueOf(localDate));

                int affectedRows = createAccount.executeUpdate();

                if (affectedRows != 1)
                    System.out.println("Couldn't create account");

                try (ResultSet generatedKeys = createAccount.getGeneratedKeys()){
                    if (generatedKeys.next()) {
                        account.setId(generatedKeys.getLong(1));
                        account.setName(generatedKeys.getString(2));
                        account.setInitialbalance(generatedKeys.getDouble(3));
                        account.setUserid(generatedKeys.getLong(4));
                        account.setCreatedat(generatedKeys.getDate(5));
                    } else {
                        throw  new SQLException("Couldn't find id");
                    }
                }

            }
            close();
            return account;
    }

    public void deleteAccount(String token, Long accountId)throws SQLException {

        listAccountById(token,accountId);
        Long idAccount = tokenUtil.verifyJwt(token);

        if (open()) {

            deleteAccount.setLong(1, idAccount);
            deleteAccount.setLong(2, accountId);

            int affectedRows = deleteAccount.executeUpdate();

            if (affectedRows != 0) {
                System.out.println("Account deleted");
            } else {
                throw new InvalidTokenException("Unauthorized user");
            }
            close();
        }
    }
    public Account updateAccount(String token, Long accountId, Account account) throws SQLException{

        Long userId = tokenUtil.verifyJwt(token);
        listAccountById(token,accountId);
        AccountValidationService.accountFieldsValidation(account);
        userService.listUserById(account.getUserid());

        if (open()){

            updateAccount.setString(1, account.getName());
            updateAccount.setDouble(2, account.getInitialbalance());
            updateAccount.setLong(3, account.getUserid());
            LocalDate localDate = LocalDate.now();
            updateAccount.setDate(4, Date.valueOf(localDate));
            updateAccount.setLong(5, accountId);
            updateAccount.setLong(6, userId);

            int affectedRows = updateAccount.executeUpdate();

            if (affectedRows > 0){
                System.out.println("Account was updated");
            }else {
                throw new InvalidTokenException("Can't update account, unauthorized userId");
            }
        }
        close();
        return null;
    }
}
