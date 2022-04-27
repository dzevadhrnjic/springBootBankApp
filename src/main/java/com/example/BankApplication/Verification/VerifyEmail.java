package com.example.BankApplication.Verification;

import com.example.BankApplication.user.User;
import com.example.BankApplication.user.UserRepository;
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

        userByMail.setVerifyEmail(true);
        userRepository.save(userByMail);

        emailVerificationRepository.save(verificationMail);


        return verificationMail;

    }

}
