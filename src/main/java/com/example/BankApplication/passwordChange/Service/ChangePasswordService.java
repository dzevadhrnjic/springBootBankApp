package com.example.BankApplication.passwordChange.Service;

import com.example.BankApplication.passwordChange.Model.ChangePassword;
import com.example.BankApplication.user.*;
import com.example.BankApplication.verification.EmailVerificationRepository;
import com.example.BankApplication.verification.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {

    TokenUtil tokenUtil = new TokenUtil();
    Verification verification = new Verification();
    HashUtils hashUtils = new HashUtils();

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    EmailVerificationRepository emailVerificationRepository;

    public void changePassword(String token) throws Exception {

        Long userId = tokenUtil.verifyJwt(token);
        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new ValidationIdException("Invalid email or password, please try again");
        }

        String code = emailService.getRandomNumbers();
        emailService.sendEmail(user.getEmail(), code, "Change your password");

        verification.setEmail(user.getEmail());
        verification.setCode(code);

        emailVerificationRepository.save(verification);
    }

    public ChangePassword newPassword(String token, ChangePassword changePassword) {

        Long userId = tokenUtil.verifyJwt(token);
        User user = userRepository.getUserById(userId);
        User password = userRepository.getUserByPassword(hashUtils.generateHash(changePassword.getOldPassword()));
        Verification verification = emailVerificationRepository.getEmailAndCode(user.getEmail(), changePassword.getCode());

        if (verification == null || password == null) {
            throw new InvalidEmailOrPasswordException("Invalid code or password, try again");
        }

        user.setPassword(hashUtils.generateHash(changePassword.getNewPassword()));

        userRepository.save(user);

        return null;
    }
}

