package com.example.BankApplication.verification.Service;

import com.example.BankApplication.user.Model.User;
import com.example.BankApplication.user.Database.UserRepository;
import com.example.BankApplication.verification.Exception.EmailVerificationException;
import com.example.BankApplication.verification.Database.EmailVerificationRepository;
import com.example.BankApplication.verification.Model.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerifyEmail {

    @Autowired
    EmailVerificationRepository emailVerificationRepository;

    @Autowired
    UserRepository userRepository;

    public Verification emailVerify(Verification verification) {

        Verification verificationMail = emailVerificationRepository.getEmailAndCode(verification.getEmail(),
                verification.getCode());
        User userByMail = userRepository.getUserByEmail(verification.getEmail());

        if (verificationMail == null) {
            throw new EmailVerificationException("Couldn't verify, wrong email or confirmation code");
        }

        verificationMail.setEmail(verificationMail.getEmail());
        verificationMail.setCode(verificationMail.getCode());

        userByMail.setVerifyemail(true);
        userRepository.save(userByMail);

        emailVerificationRepository.save(verificationMail);

        return verificationMail;
    }
}
