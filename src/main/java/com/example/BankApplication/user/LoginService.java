package com.example.BankApplication.user;

import java.sql.*;

public class LoginService {

    HashUtils hashUtils = new HashUtils();
    TokenUtil tokenUtil = new TokenUtil();
    User user = new User();


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
    public static final String COLUMN_TOKEN = "accessToken";

    public static final int INDEX_USER_ID = 1;
    public static final int INDEX_USER_EMAIL = 2;
    public static final int INDEX_USER_PASSWORD = 3;
    public static final int INDEX_USER_TOKEN = 4;

    public static final String LOGIN_USER = " SELECT " + COLUMN_USER_ID + " FROM " +
            TABLE_USER + " WHERE " + COLUMN_USER_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";

    private Connection connection;
    private PreparedStatement loginUser;

    public boolean open() {
        try {
            connection = DriverManager.getConnection(URL, "postgres", "kovilica1234");
            loginUser = connection.prepareStatement(LOGIN_USER);
            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database " + e);
            return false;
        }
    }

    public void close() {
        try {
            if (loginUser != null) {
                loginUser.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't close connection");
        }
    }

    public AccessToken loginUser(User user) throws SQLException {

        AccessToken accessToken = new AccessToken();

        if (open()) {

            try {
                loginUser.setString(1, user.getEmail());
                loginUser.setString(2,  hashUtils.generateHash(user.getPassword()));

                ResultSet results  = loginUser.executeQuery();

                if(results.next()) {
                    Long userId = results.getLong(INDEX_USER_ID);
                    accessToken.setAccessToken(tokenUtil.jwt(userId));
                }else
                    throw new InvalidEmailOrPasswordException("Invalid email or password");
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        close();
        return accessToken;
    }
}