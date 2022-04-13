package com.example.BankApplication.user;

import com.example.BankApplication.account.InvalidTokenException;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service

public class UserService {

    HashUtils hashUtils = new HashUtils();
    TokenUtil tokenUtil = new TokenUtil();

    public static final String URL = "jdbc:postgresql://localhost:5432/bank";

    public static final String TABLE_USER = "dbuser";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_FIRST_NAME = "firstname";
    public static final String COLUMN_USER_LAST_NAME = "lastname";
    public static final String COLUMN_USER_ADDRESS = "address";
    public static final String COLUMN_USER_PHONE_NUMBER = "phonenumber";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_CREATED_AT = "createdat";
    public static final String COLUMN_PASSWORD = "password";

    public static final int INDEX_USER_ID = 1;
    public static final int INDEX_USER_FIRST_NAME = 2;
    public static final int INDEX_USER_LAST_NAME = 3;
    public static final int INDEX_USER_ADDRESS = 4;
    public static final int INDEX_USER_PHONE_NUMBER = 5;
    public static final int INDEX_USER_EMAIL = 6;
    public static final int INDEX_USER_CREATED_AT = 7;


    public static final String USERS = "SELECT " + COLUMN_USER_ID + ", " + COLUMN_USER_FIRST_NAME + ", "
            + COLUMN_USER_LAST_NAME + ", " + COLUMN_USER_ADDRESS + ", " + COLUMN_USER_PHONE_NUMBER + ", "
            + COLUMN_USER_EMAIL + ", " + COLUMN_USER_CREATED_AT + " FROM " + TABLE_USER + " WHERE "
            + COLUMN_USER_ID + " = ? ";

    public static final String USER_BY_ID = "SELECT " + COLUMN_USER_ID + ", " + COLUMN_USER_FIRST_NAME + ", "
            + COLUMN_USER_LAST_NAME + ", " + COLUMN_USER_ADDRESS + ", " + COLUMN_USER_PHONE_NUMBER + ", "
            + COLUMN_USER_EMAIL + ", " + COLUMN_USER_CREATED_AT + " FROM " + TABLE_USER + " WHERE "
            + COLUMN_USER_ID + " = ? ";

    public static final String CREATE_USER = "INSERT INTO " + TABLE_USER + '(' +
            COLUMN_USER_FIRST_NAME + ", " + COLUMN_USER_LAST_NAME + ", " + COLUMN_USER_ADDRESS + ", "
            + COLUMN_USER_PHONE_NUMBER + ", " + COLUMN_USER_EMAIL + ", " + COLUMN_USER_CREATED_AT + ", " +
            COLUMN_PASSWORD + ") VALUES ( ?, ?, ?, ?, ?, ?, ?)";

        public static final String DELETE_USER = "DELETE FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = ? AND " +
                COLUMN_USER_ID + " = ? ";

    public static final String UPDATE_USER = "UPDATE " + TABLE_USER + " SET " + COLUMN_USER_FIRST_NAME + " =?, " +
            COLUMN_USER_LAST_NAME + " =?, " + COLUMN_USER_ADDRESS + " =?, " + COLUMN_USER_PHONE_NUMBER + " =?, " +
            COLUMN_USER_EMAIL + " =?, " + COLUMN_USER_CREATED_AT + " =?, " + COLUMN_PASSWORD + " =? "  + " WHERE " + COLUMN_USER_ID + " = ? AND "
            + COLUMN_USER_ID + " = ? ";

    private Connection connection;

    private PreparedStatement users;
    private PreparedStatement userById;
    private PreparedStatement createUser;
    private PreparedStatement deleteUser;
    private PreparedStatement updateUser;

    public boolean open() {
        try {
            connection = DriverManager.getConnection(URL, "postgres", "kovilica1234");
            users = connection.prepareStatement(USERS);
            userById = connection.prepareStatement(USER_BY_ID);
            createUser = connection.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS);
            deleteUser = connection.prepareStatement(DELETE_USER);
            updateUser = connection.prepareStatement(UPDATE_USER);
            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database " + e);
            return false;
        }
    }

    public void close() {
        try {
            if (users != null) {
                users.close();
            }
            if (userById != null) {
                userById.close();
            }
            if (createUser != null) {
                createUser.close();
            }
            if (deleteUser != null) {
                deleteUser.close();
            }
            if (updateUser != null) {
                updateUser.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't close connection");
        }
    }

    public List<User> listUsers() throws SQLException {

        if (open()) {

            StringBuilder stringBuilder = new StringBuilder("SELECT * FROM ");
            stringBuilder.append(TABLE_USER);

            try (Statement statement = connection.createStatement();
                 ResultSet results = statement.executeQuery(stringBuilder.toString())) {

                List<User> users = new ArrayList<>();

                while (results.next()) {
                    User user = new User();
                    user.setId(results.getLong(INDEX_USER_ID));
                    user.setFirstname(results.getString(INDEX_USER_FIRST_NAME));
                    user.setLastname(results.getString(INDEX_USER_LAST_NAME));
                    user.setAddress(results.getString(INDEX_USER_ADDRESS));
                    user.setPhonenumber(results.getString(INDEX_USER_PHONE_NUMBER));
                    user.setEmail(results.getString(INDEX_USER_EMAIL));
                    user.setCreatedat(results.getDate(INDEX_USER_CREATED_AT));
                    users.add(user);
                }
                close();
                return users;
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public User listUserById(Long userId) throws SQLException {

        if (open()) {

            userById.setLong(1, userId);

            ResultSet results = userById.executeQuery();
            if (!results.isBeforeFirst())
                throw new ValidationIdException("Couldn't find user with that id");

            User user = new User();

            while (results.next()) {
                user.setId(results.getLong(INDEX_USER_ID));
                user.setFirstname(results.getString(INDEX_USER_FIRST_NAME));
                user.setLastname(results.getString(INDEX_USER_LAST_NAME));
                user.setAddress(results.getString(INDEX_USER_ADDRESS));
                user.setPhonenumber(results.getString(INDEX_USER_PHONE_NUMBER));
                user.setEmail(results.getString(INDEX_USER_EMAIL));
                user.setCreatedat(results.getDate(INDEX_USER_CREATED_AT));
            }
            close();
            return user;
        }throw new SQLException("Couldn't list user");
    }


    public User createUser(User user) throws SQLException {

        UserValidationService.userFieldsValidation(user);

        if (open()) {

            createUser.setString(1, user.getFirstname());
            createUser.setString(2, user.getLastname());
            createUser.setString(3, user.getAddress());
            createUser.setString(4, user.getPhonenumber());
            createUser.setString(5, user.getEmail());
            LocalDate localDate = LocalDate.now();
            createUser.setDate(6, Date.valueOf(localDate));
            createUser.setString(7,hashUtils.generateHash(user.getPassword()));


            int affectedRows = createUser.executeUpdate();

            if (affectedRows != 1) {
                System.out.println("Couldn't create user");
            }

            try (ResultSet generatedKeys = createUser.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    user.setFirstname(generatedKeys.getString(2));
                    user.setLastname(generatedKeys.getString(3));
                    user.setAddress(generatedKeys.getString(4));
                    user.setPhonenumber(generatedKeys.getString(5));
                    user.setEmail(generatedKeys.getString(6));
                    user.setCreatedat(generatedKeys.getDate(7));
                } else {
                    throw new SQLException("Couldn't get the id");
                }
            }
        }
        close();
        return user;
    }

    public void deleteUser(String token, Long userId) throws SQLException {

        listUserById(userId);
        Long idUser = tokenUtil.verifyJwt(token);

        if (open()) {

                deleteUser.setLong(1, userId);
                deleteUser.setLong(2, idUser);

                int affectedRows = deleteUser.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("User was deleted");
                }else {
                    throw new InvalidTokenException("Unauthorized user");
            }
        }
        close();
    }

    public User updateUser(String token, Long userId, User user) throws SQLException {

        listUserById(userId);
        UserValidationService.userFieldsValidation(user);
        Long idUser = tokenUtil.verifyJwt(token);

        if (open()) {

            updateUser.setString(1, user.getFirstname());
            updateUser.setString(2, user.getLastname());
            updateUser.setString(3, user.getAddress());
            updateUser.setString(4, user.getPhonenumber());
            updateUser.setString(5, user.getEmail());
            LocalDate localDate = LocalDate.now();
            updateUser.setDate(6, Date.valueOf(localDate));
            updateUser.setString(7,hashUtils.generateHash(user.getPassword()));
            updateUser.setLong(8, userId);
            updateUser.setLong(9, idUser);

            int affectedRows = updateUser.executeUpdate();

            if (affectedRows != 0) {
                System.out.println("User was updated");
            } else {
                throw new InvalidTokenException("Couldn't update user, unauthorized id");
            }
        }
        close();
        return null;
    }
}
