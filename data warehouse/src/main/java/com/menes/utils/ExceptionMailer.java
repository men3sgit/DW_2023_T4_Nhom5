package com.menes.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class ExceptionMailer {
    public static String TO_EMAIL = "20130072@st.hcmuaf.edu.vn";
    private static final Configuration CONFIGURATION = Configuration.getInstance();

    public static void handleException(Exception e) {
        try {
            // Create an error file and write the exception details to it
            String errorFileName = CONFIGURATION.getErrorPath() + "error_log" + DateTimeFormatter.ofPattern("dd-MM-yy_HH-mm-ss").format(LocalDateTime.now()) + ".txt";
            PrintWriter writer = new PrintWriter(new FileWriter(errorFileName));
            e.printStackTrace(writer);
            writer.close();

            // Send the error file via email
            sendEmailWithAttachment(errorFileName);

            System.err.println("Error file sent successfully.");
        } catch (Exception ex) {
            // Handle any exceptions that might occur during the handling process
            ex.printStackTrace();
        }
    }

    private static void sendEmailWithAttachment(String attachmentFilePath) throws MessagingException, IOException {
        // Set up mail properties
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // replace with your SMTP server
        properties.put("mail.smtp.port", "465"); // replace with your SMTP server port
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Create a mail session
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(CONFIGURATION.getServerMailUsername(), CONFIGURATION.getServerMailPassword());
            }
        });

        // Create a MIME message
        MimeMessage message = new MimeMessage(session);

        // Set the sender and recipient addresses
        message.setFrom(new InternetAddress(CONFIGURATION.getServerMailUsername()));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(TO_EMAIL));

        // Set the email subject
        message.setSubject("Error Report");

        // Create the email body text
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("An exception occurred. Please find the error file attached.");

        // Create the attachment
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(attachmentFilePath);

        // Combine the email body and attachment
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachmentPart);

        // Set the content of the message
        message.setContent(multipart);

        // Send the email
        Transport.send(message);
    }
}
