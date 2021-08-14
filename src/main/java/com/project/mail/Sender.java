package com.project.mail;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Properties;

@Service
@Controller
public class Sender
{

    public static void addFile(String filename, Multipart multipart,String name) throws MessagingException {
        DataSource source = new FileDataSource(filename);
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(name);
        multipart.addBodyPart(messageBodyPart);
    }
    public static void SendStatus(String to,String status)
    {
        String from = "zetchenkovlads2@gmail.com";
        Properties props = new Properties();
        props.put("mail.smtp.user","username");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "25");
        props.put("mail.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.EnableSSL.enable","true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from,"qazwsxcde123");
                    }
                });
        try{
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Mail Body");
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Scoring System: Вашу заявку було розглянуто");
            message.setText(status);
            Transport.send(message);
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
    public static void SendPassword(String to, String password)
    {
        String from = "zetchenkovlads2@gmail.com";
        Properties props = new Properties();
        props.put("mail.smtp.user","username");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "25");
        props.put("mail.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.EnableSSL.enable","true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from,"qazwsxcde123");
                    }
                });
        try{
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Mail Body");
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Ваш пароль для облікового запису Scoring System");
            message.setText(password);
            Transport.send(message);
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
    public static void Send(String dear, String to, List<String> list)
    {
        String from = "zetchenkovlads2@gmail.com";
        Properties props = new Properties();
        props.put("mail.smtp.user","username");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "25");
        props.put("mail.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.EnableSSL.enable","true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from,"qazwsxcde123");
                    }
                });
        try{
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Scoring System");
            Multipart multipart = new MimeMultipart();
            int i=1;
            for (String file : list) {
                addFile(file,multipart,Integer.toString(i));
                i++;
            }
            multipart.addBodyPart(messageBodyPart);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            System.out.println(to);
            System.out.println(Message.RecipientType.TO);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Заявка на розгляд "+dear+" ");
            message.setText("");
            message.setContent(multipart);
            Transport.send(message);
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}