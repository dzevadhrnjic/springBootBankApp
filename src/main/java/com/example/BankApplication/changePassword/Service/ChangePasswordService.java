package com.example.BankApplication.changePassword.Service;

import com.example.BankApplication.changePassword.Model.ChangePassword;
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
        Verification verification = emailVerificationRepository.getEmailAndCode(user.getEmail(), changePassword.getCode());

        if (verification == null) {
            throw new InvalidEmailOrPasswordException("Invalid code or password, try again");
        }

        if (!user.getPassword().equals(hashUtils.generateHash(changePassword.getOldPassword()))) {
            throw new InvalidEmailOrPasswordException("Invalid password, try again");
        } else {
            user.setPassword(hashUtils.generateHash(changePassword.getNewPassword()));

            userRepository.save(user);

            return null;
        }
    }
}
