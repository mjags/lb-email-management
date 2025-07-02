package com.callcenter.emailmanagement.service;

import com.callcenter.emailmanagement.domain.model.Email;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class OutlookEmailService {
    private static final Logger logger = LoggerFactory.getLogger(OutlookEmailService.class);
    
    @Value("${app.email.username}")
    private String emailUsername;
    
    @Value("${app.email.password}")
    private String emailPassword;
    
    @Value("${app.email.host:outlook.office365.com}")
    private String emailHost;
    
    @Value("${app.email.port:993}")
    private int emailPort;
    
    public List<Email> fetchEmailsFromFolder(String folderName) {
        logger.info("🔍 Fetching emails from folder: {}", folderName);
        List<Email> emails = new ArrayList<>();
        
        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", emailHost);
            props.put("mail.imaps.port", emailPort);
            props.put("mail.imaps.ssl.enable", true);
            
            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");
            store.connect(emailHost, emailUsername, emailPassword);
            
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            
            Message[] messages = folder.getMessages();
            logger.info("📧 Found {} messages in folder", messages.length);
            
            // Get only unread messages for processing
            for (Message message : messages) {
                if (!message.isSet(Flags.Flag.SEEN)) {
                    Email email = convertToEmail(message);
                    emails.add(email);
                    logger.info("📨 Converted message: {} from {}", email.getSubject(), email.getFromAddress());
                }
            }
            
            folder.close(false);
            store.close();
            
            logger.info("✅ Successfully fetched {} new emails", emails.size());
            
        } catch (Exception e) {
            logger.error("❌ Error fetching emails: {}", e.getMessage(), e);
        }
        
        return emails;
    }
    
    public void sendEmail(Email email) {
        logger.info("📤 Sending email: {} to {}", email.getSubject(), email.getToAddress());
        
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.office365.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailUsername, emailPassword);
                }
            });
            
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getToAddress()));
            message.setSubject(email.getSubject());
            
            if (email.getHtmlContent() != null) {
                message.setContent(email.getHtmlContent(), "text/html");
            } else {
                message.setText(email.getTextContent());
            }
            
            Transport.send(message);
            email.setSentDate(LocalDateTime.now());
            email.setDirection(Email.EmailDirection.OUTBOUND);
            
            logger.info("✅ Email sent successfully: {}", email.getSubject());
            
        } catch (Exception e) {
            logger.error("❌ Error sending email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    private Email convertToEmail(Message message) throws MessagingException {
        Email email = new Email();
        
        email.setMessageId(message.getHeader("Message-ID") != null ? message.getHeader("Message-ID")[0] : "unknown");
        email.setSubject(message.getSubject());
        email.setFromAddress(message.getFrom()[0].toString());
        
        if (message.getRecipients(Message.RecipientType.TO) != null) {
            email.setToAddress(message.getRecipients(Message.RecipientType.TO)[0].toString());
        }
        
        email.setReceivedDate(message.getReceivedDate() != null ? 
            message.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : 
            LocalDateTime.now());
        
        email.setDirection(Email.EmailDirection.INBOUND);
        email.setPriority(convertPriority(message));
        
        try {
            if (message.isMimeType("text/plain")) {
                email.setTextContent((String) message.getContent());
            } else if (message.isMimeType("text/html")) {
                email.setHtmlContent((String) message.getContent());
            } else if (message.isMimeType("multipart/*")) {
                extractMultipartContent(message, email);
            }
        } catch (Exception e) {
            logger.warn("Could not extract email content: {}", e.getMessage());
            email.setTextContent("Content could not be extracted");
        }
        
        return email;
    }
    
    private void extractMultipartContent(Message message, Email email) throws Exception {
        Multipart multipart = (Multipart) message.getContent();
        
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            
            if (bodyPart.isMimeType("text/plain")) {
                email.setTextContent((String) bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                email.setHtmlContent((String) bodyPart.getContent());
            }
        }
    }
    
    private Email.EmailPriority convertPriority(Message message) {
        try {
            String[] priority = message.getHeader("X-Priority");
            if (priority != null && priority.length > 0) {
                switch (priority[0]) {
                    case "1": return Email.EmailPriority.URGENT;
                    case "2": return Email.EmailPriority.HIGH;
                    case "4": case "5": return Email.EmailPriority.LOW;
                    default: return Email.EmailPriority.NORMAL;
                }
            }
        } catch (Exception e) {
            logger.debug("Could not determine email priority: {}", e.getMessage());
        }
        return Email.EmailPriority.NORMAL;
    }
}