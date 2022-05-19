package com.example.BankApplication.user.Service;

import com.example.BankApplication.user.Database.UserRepository;
import com.example.BankApplication.user.Exception.ValidationIdException;
import com.example.BankApplication.user.Model.User;
import com.example.BankApplication.user.Util.HashUtils;
import com.example.BankApplication.user.Validation.UserValidationService;
import com.example.BankApplication.verification.Database.EmailVerificationRepository;
import com.example.BankApplication.verification.Model.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    HashUtils hashUtils = new HashUtils();
    Verification verification = new Verification();

    @Autowired
    EmailVerificationRepository emailVerificationRepository;

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

    public User createUser(User user) throws MessagingException, IOException {

        UserValidationService.userFieldsValidation(user);

        LocalDateTime localDateAndTime = LocalDateTime.now();
        user.setCreatedat(localDateAndTime);
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

        LocalDateTime localDateAndTime = LocalDateTime.now();
        user.setCreatedat(localDateAndTime);
        user.setPassword(hashUtils.generateHash(user.getPassword()));
        user.setId(userId);

        userRepository.save(user);

        return user;
    }
}
