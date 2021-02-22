package game.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.mail.*;
import javax.mail.internet.*;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Properties;

public class MailUtil {

    public static void sendFeedbackMail(String senderName, String senderMail, String senderMsg)
            throws Exception {
        final String sender = "lh-funmail3@web.de";
        final String receiver = "l.hilfrich@gmx.de";

        // please don't play around with this, the sender email I used is useless anyways ...
        String auth = new D().doMagic("PKmumpJrL8V+LtVp9RvuWYklKlCX19Qm");

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "smtp.web.de");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.user", sender);
        properties.put("mail.smtp.password", auth);
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

    private static class D {

        private static final String UNICODE_FORMAT = "UTF8";
        public static final String DESEDE_ENC_SCHEME = "DESede";
        private KeySpec ks;
        private SecretKeyFactory skf;
        private Cipher cipher;
        byte[] arrayBytes;
        SecretKey key;

        public D() throws Exception {
            String myKey = "ThisIsSpartaThisIsSparta";
            String myScheme = DESEDE_ENC_SCHEME;
            arrayBytes = myKey.getBytes(UNICODE_FORMAT);
            ks = new DESedeKeySpec(arrayBytes);
            skf = SecretKeyFactory.getInstance(myScheme);
            cipher = Cipher.getInstance(myScheme);
            key = skf.generateSecret(ks);
        }

        public String doMagic(String encryptedString) {
            String magicText = null;
            try {
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] encText = Base64.getDecoder().decode(encryptedString);
                byte[] plainText = cipher.doFinal(encText);
                magicText = new String(plainText);
            } catch (Exception ignored) {
            }
            return magicText;
        }

    }

}
