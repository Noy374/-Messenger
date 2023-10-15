package com.example.messenger.service;


import com.example.messenger.entity.Email;
import com.example.messenger.entity.User;
import com.example.messenger.exceptions.EmailTokenNotFoundException;
import com.example.messenger.repositorys.EmailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;
    @Value("${email.username}")
    private String emailUsername;
    @Value("${email.password}")
    private String emailPassword;
    @Transactional
    public void saveEmail(Email email) {
            emailRepository.save(email);
    }
    public boolean checkUserByEmail(String email) {
        return emailRepository.getEmailByEmailAddress(email).orElse(null) == null;
    }

    public boolean sendConfirmationEmail(String recipientEmail) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.mail.ru");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(emailUsername,emailPassword);
                        }
                    });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Email address confirmation");

            String emailContent = "To confirm your email address, please follow the link: "
                    +generateConfirmationLink(recipientEmail);

            message.setText(emailContent);

            Transport.send(message);
            log.info("A confirmation email has been sent to " + recipientEmail);
        }
        catch (MessagingException e) {
            log.error("Error occurred while sending email", e);
            return false;
        }
        return true;
    }


    private String generateConfirmationLink(String email){

        String confirmationToken = UUID.randomUUID().toString();

        emailRepository.updateTokenByEmailAddress(email,confirmationToken);


        return "http://localhost:8080/confirm?token=" + confirmationToken;
    }

    @Transactional
    public void confirmEmailToken(String token) throws EmailTokenNotFoundException {
        try {
            emailRepository.updateStatusByToken(token, true);
        }catch (Exception e){
            throw new EmailTokenNotFoundException();
        }

    }

    @Transactional
    public void updateEmail(User user, String email) {
        emailRepository.updateEmailByUser(user,email);
    }
}
