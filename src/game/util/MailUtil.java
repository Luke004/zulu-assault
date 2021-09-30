package game.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class MailUtil {

    public static void sendFeedbackMail(String senderName, String senderMail, String senderMsg)
            throws Exception {
        final String sender = "lh-funmail3@web.de";
        final String receiver = "l.hilfrich@gmx.de";

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "smtp.web.de");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.user", sender);
        properties.put("mail.smtp.password", "9%uk/S6-BZhC3s_");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.web.de");

        Session mailSession = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("mail.smtp.user"),
                        properties.getProperty("mail.smtp.password"));
            }
        });

        Message message = new MimeMessage(mailSession);
        InternetAddress addressTo = new InternetAddress(receiver);
        message.setRecipient(Message.RecipientType.TO, addressTo);
        message.setFrom(new InternetAddress(sender));
        message.setSubject("New feedback from " + (senderName.isEmpty() ? "Anonymous" : senderName) + "!");
        message.setContent(senderMsg + "\n\nSender Mail: " + senderMail, "text/plain");
        Transport.send(message);
    }

}
