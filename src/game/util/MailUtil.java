package game.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class MailUtil {

    private static final String FEEDBACK_SENDER = "f.zuluassault@web.de";
    private static final String FEEDBACK_RECEIVER = "l.hilfrich@gmx.de";

    public static void sendFeedbackMail(String senderName, String senderMail, String senderMsg)
            throws Exception {

        Properties properties = getProperties();

        Session mailSession = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("mail.smtp.user"),
                        properties.getProperty("mail.smtp.password"));
            }
        });

        Message message = new MimeMessage(mailSession);
        InternetAddress addressTo = new InternetAddress(FEEDBACK_RECEIVER);
        message.setRecipient(Message.RecipientType.TO, addressTo);
        message.setFrom(new InternetAddress(FEEDBACK_SENDER));
        message.setSubject("New feedback from " + (senderName.isEmpty() ? "Anonymous" : senderName) + "!");
        message.setContent(senderMsg + "\n\nSender Mail: " + senderMail, "text/plain");
        Transport.send(message);
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "smtp.web.de");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.user", FEEDBACK_SENDER);
        properties.put("mail.smtp.password", "vE9ZZ':C2C7*vqM");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.web.de");
        return properties;
    }
}
