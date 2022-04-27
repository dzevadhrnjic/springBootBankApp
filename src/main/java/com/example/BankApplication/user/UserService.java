package com.example.BankApplication.user;

import com.example.BankApplication.Verification.EmailVerificationRepository;
import com.example.BankApplication.Verification.Verification;
import com.example.BankApplication.Verification.VerifyEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service

public class UserService {

    HashUtils hashUtils = new HashUtils();
    TokenUtil tokenUtil = new TokenUtil();
    Verification verification = new Verification();

    @Autowired
    EmailVerificationRepository emailVerificationRepository;

    @Autowired
    VerifyEmail verifyEmail;

    @Autowired
    EmailService emailService;

    @Autowired
    UserRepository userRepository;

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User listUserById(Long userId) {

        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new ValidationIdException("Couldn't find user with that id");
        }

        return user;
    }

//    public User listUserByEmail(String email){
//
//        User user = userRepository.getUserByEmail(email);
//
//        if (user == null){
//            throw new ValidationException("Couldn't find user");
//        }
//
//        return user;
//    }

    public User createUser(User user) {

        UserValidationService.userFieldsValidation(user);

        LocalDate localDate = LocalDate.now();
        user.setCreatedat(Date.valueOf(localDate));
        user.setPassword(hashUtils.generateHash(user.getPassword()));

        userRepository.save(user);

        String code = emailService.getRandomNumbers();

        emailService.sendEmail(user.getEmail(), code,
                            "User " + user.getFirstname() + ", welcome to bank application");

        verification.setEmail(user.getEmail());
        verification.setCode(code);

        emailVerificationRepository.save(verification);


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
