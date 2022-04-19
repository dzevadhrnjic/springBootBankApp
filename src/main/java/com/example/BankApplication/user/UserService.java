package com.example.BankApplication.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service

public class UserService {

    HashUtils hashUtils = new HashUtils();
    TokenUtil tokenUtil = new TokenUtil();

    @Autowired
    UserRepository userRepository;

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

        LocalDate localDate = LocalDate.now();
        user.setCreatedat(Date.valueOf(localDate));
        user.setPassword(hashUtils.generateHash(user.getPassword()));

        userRepository.save(user);

        return user;
    }

    public void deleteUser(Long userId) {

        listUserById(userId);

        userRepository.deleteById(userId);

    }

    public User updateUser(Long userId, User user) {

        listUserById(userId);
        UserValidationService.userFieldsValidation(user);

        LocalDate localDate = LocalDate.now();
        user.setCreatedat(Date.valueOf(localDate));
        user.setPassword(hashUtils.generateHash(user.getPassword()));
        user.setId(userId);

        userRepository.save(user);

        return user;
    }
}
