package com.example.BankApplication.user.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Random;

@Service
public class EmailService  {

    private static final String NOREPLY_ADDRESS = "noreply@gmail.com";

    @Autowired
    public JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String text) throws MessagingException, IOException {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(NOREPLY_ADDRESS);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);
        System.out.println("Mail sent successfully");
    }

    public  void sendEmailWithAttachment(String to, String subject, String body) throws MessagingException, IOException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        mimeMessage.setFrom(new InternetAddress("from@gmail.com"));
        mimeMessage.setSubject(subject);
        mimeMessage.setText(body);

        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(new File("E://BankApplication/transactionStatement.pdf"));

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(attachmentPart);

        mimeMessage.setContent(multipart);
        javaMailSender.send(mimeMessage);

        System.out.println("Mail with attachment sent successfully");

    }

    public String getRandomNumbers(){
        Random random = new Random();
        int number = random.nextInt(999999);

        return String.format("%06d", number);
    }
}
