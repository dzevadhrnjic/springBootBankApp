package com.example.BankApplication.user.Service;

import com.example.BankApplication.user.Database.UserRepository;
import com.example.BankApplication.user.Database.UserSpecification;
import com.example.BankApplication.user.Exception.ValidationIdException;
import com.example.BankApplication.user.Model.User;
import com.example.BankApplication.user.Util.HashUtils;
import com.example.BankApplication.user.Validation.UserValidationService;
import com.example.BankApplication.verification.Database.EmailVerificationRepository;
import com.example.BankApplication.verification.Model.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

import org.springframework.data.domain.Pageable;

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

    public List<User> listUsers(Integer pageNumber, Integer pageSize, String firstname, String lastname, String address, String email) {

        Pageable paging = PageRequest.of(pageNumber, pageSize);

        Specification<User> specification = Specification.where(null);

        if (firstname != null && !firstname.isEmpty()) {
            specification = specification.and(UserSpecification.hasFirstName(firstname));
        }

        if (lastname != null && !lastname.isEmpty()) {
            specification = specification.and(UserSpecification.hasLastName(lastname));
        }

        if (address != null && !address.isEmpty()) {
            specification = specification.and(UserSpecification.hasAddress(address));
        }

        if (email != null && !email.isEmpty()) {
            specification = specification.and(UserSpecification.hasEmail(email));
        }

        return userRepository.findAll(specification, paging);
    }

    public User getUserById(Long userId) {

        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new ValidationIdException("Couldn't find user with that id");
        }

        return user;
    }

    public User createUser(User user) throws MessagingException, IOException {

        UserValidationService.userFieldsValidation(user);

        LocalDateTime localDateAndTime = LocalDateTime.now();
        user.setCreatedAt(localDateAndTime);
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

        getUserById(userId);

        userRepository.deleteById(userId);

    }

    public User updateUser(Long userId, User user) {

        getUserById(userId);
        UserValidationService.userFieldsValidation(user);

        LocalDateTime localDateAndTime = LocalDateTime.now();
        user.setCreatedAt(localDateAndTime);
        user.setPassword(hashUtils.generateHash(user.getPassword()));
        user.setId(userId);

        userRepository.save(user);

        return user;
    }
}
