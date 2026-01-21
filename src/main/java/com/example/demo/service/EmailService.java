package com.example.demo.service;


import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmailService {

    public void sendEmails(String username, String password,
                           String subject, String message,
                           MultipartFile recipientsFile,
                           MultipartFile attachmentFile) throws Exception {

        // Read emails from uploaded file
        Set<String> emails;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(recipientsFile.getInputStream()))) {
            emails = reader.lines()
                    .map(String::trim)
                    .filter(e -> !e.isEmpty())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        // Create SMTP session
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        session.setDebug(true);

        // Send emails
        for (String to : emails) {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(username));
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setSubject(subject);

            // Body part
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(message);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);

            // Attachment (if present)
            if (attachmentFile != null && !attachmentFile.isEmpty()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                File temp = File.createTempFile("attachment-", attachmentFile.getOriginalFilename());
                attachmentFile.transferTo(temp);
                attachmentPart.attachFile(temp);
                multipart.addBodyPart(attachmentPart);
            }

            mimeMessage.setContent(multipart);
            Transport.send(mimeMessage);
        }
    }
}
