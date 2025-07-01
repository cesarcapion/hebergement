package fr.epita.assistants.ping.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

@ApplicationScoped
public class EmailService {

    private static final String SENDER_EMAIL = "ticketaka@zohomail.eu";
    private static final String APP_PASSWORD = "bXcstWTNPqfY\n";
    private static final String SMTP_SERVER = "smtp.zoho.eu";
    private static final int SMTP_PORT = 465;

    public void dispatchResetLink(String recipientEmail, String link) {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_SERVER);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Password Reset");
            message.setText("Click on the following link to reset your password :\n\n" + link);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
