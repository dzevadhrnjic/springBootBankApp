package com.example.BankApplication.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService  {

    private static final String NOREPLY_ADDRESS = "noreply@gmail.com";

    @Autowired
    public JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(NOREPLY_ADDRESS);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);

        System.out.println("Mail sent successfully");
    }

    public String getRandomNumbers(){
        Random random = new Random();
        int number = random.nextInt(999999);

        return String.format("%06d", number);
    }
}
