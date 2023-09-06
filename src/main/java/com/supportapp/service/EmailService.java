package com.supportapp.service;

import com.sun.mail.smtp.SMTPTransport;
import com.supportapp.constant.EmailConstant;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Properties;

@Service
public class EmailService {
    public void SendNewPasswordEmail(String firstName, String password, String email) throws MessagingException {
        Message message = createEmail(firstName, password, email);
        Transport smtpTransport = (Transport) getEmailSession().getTransport(EmailConstant.SIMPLE_EMAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(EmailConstant.GMAIL_SMTP_SERVER, EmailConstant.USERNAME, EmailConstant.PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();


    }
    private Message createEmail(String firstName, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(EmailConstant.FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(EmailConstant.CC_EMAIL, false));
        message.setSubject(EmailConstant.EMAIL_SUBJECT);
        message.setText("Hello " + firstName + " \n\n Tu nuevo password es: " + password + " \n\n Atte Soporte Tecnico");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }
    private Session getEmailSession(){
        Properties properties = System.getProperties();
        properties.put(EmailConstant.SMTP_HOST, EmailConstant.GMAIL_SMTP_SERVER);
        properties.put(EmailConstant.SMTP_AUTH, true);
        properties.put(EmailConstant.SMTP_PORT, EmailConstant.DEFGAULT_PORT);
        properties.put(EmailConstant.SMTP_STARTTLS_ENABLE, true);
        properties.put(EmailConstant.SMTP_STARTTLS_REQUIRED, true);

        return Session.getInstance(properties, null);
    }
}
