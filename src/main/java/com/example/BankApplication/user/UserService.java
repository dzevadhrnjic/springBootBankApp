package com.example.BankApplication.user;

import com.example.BankApplication.account.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

@Service

public class UserService {

    HashUtils hashUtils = new HashUtils();
    TokenUtil tokenUtil = new TokenUtil();

    private final UserRepository userRepository;
    @Autowired

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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


//    public static final String DELETE_USER = "DELETE FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = ? AND " +
//                COLUMN_USER_ID + " = ? ";

    public static final String UPDATE_USER = "UPDATE " + TABLE_USER + " SET " + COLUMN_USER_FIRST_NAME + " =?, " +
            COLUMN_USER_LAST_NAME + " =?, " + COLUMN_USER_ADDRESS + " =?, " + COLUMN_USER_PHONE_NUMBER + " =?, " +
            COLUMN_USER_EMAIL + " =?, " + COLUMN_USER_CREATED_AT + " =?, " + COLUMN_PASSWORD + " =? "  + " WHERE " + COLUMN_USER_ID + " = ? AND "
            + COLUMN_USER_ID + " = ? ";

    private Connection connection;

    private PreparedStatement deleteUser;
    private PreparedStatement updateUser;

    public boolean open() {
        try {
            connection = DriverManager.getConnection(URL, "postgres", "kovilica1234");
//            deleteUser = connection.prepareStatement(DELETE_USER);
            updateUser = connection.prepareStatement(UPDATE_USER);
            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database " + e);
            return false;
        }
    }

    public void close() {
        try {
//            if (deleteUser != null) {
//                deleteUser.close();
//            }
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

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User listUserById(Long userId) {

        User user = userRepository.getUserById(userId);

        if (user == null){
            throw new ValidationIdException("Couldn't find user with that id");
        }

        return user;
    }

    public User createUser(User user) {

        UserValidationService.userFieldsValidation(user);

        user.setFirstname(user.getFirstname());
        user.setLastname(user.getLastname());
        user.setAddress(user.getAddress());
        user.setPhonenumber(user.getPhonenumber());
        user.setEmail(user.getEmail());
        LocalDate localDate = LocalDate.now();
        user.setCreatedat(Date.valueOf(localDate));
        user.setPassword(hashUtils.generateHash(user.getPassword()));

        userRepository.save(user);

        return user;
    }

    public void deleteUser(String token, Long userId) {

        Long idUser = tokenUtil.verifyJwt(token);
        listUserById(userId);

        userRepository.deleteById(idUser);

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
