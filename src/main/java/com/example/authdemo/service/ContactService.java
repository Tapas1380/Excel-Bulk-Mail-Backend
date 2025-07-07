package com.example.authdemo.service;

import com.example.authdemo.dto.ContactMessage;
import com.example.authdemo.model.Contact;
import com.example.authdemo.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.admin.email:tapasranjanhr@gmail.com}")
    private String adminEmail;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    /**
     * Save contact message to database and send email notification
     */
    public Contact saveMessage(ContactMessage contactMessage) {
        try {
            // Create entity from DTO
            Contact contact = new Contact();
            contact.setName(contactMessage.getName());
            contact.setEmail(contactMessage.getEmail());
            contact.setSubject(contactMessage.getSubject());
            contact.setMessage(contactMessage.getMessage());
            contact.setCreatedAt(LocalDateTime.now());
            contact.setStatus("NEW");

            // Save to database
            Contact savedContact = contactRepository.save(contact);

            // Send email notification (optional)
            if (mailEnabled) {
                sendEmailNotification(contactMessage);
            }

            return savedContact;

        } catch (Exception e) {
            throw new RuntimeException("Failed to save contact message: " + e.getMessage(), e);
        }
    }

    /**
     * Get all contact messages
     */
    public List<Contact> getAllMessages() {
        return contactRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get contact message by ID
     */
    public Optional<Contact> getMessageById(Long id) {
        return contactRepository.findById(id);
    }

    /**
     * Update message status
     */
    public Contact updateMessageStatus(Long id, String status) {
        Optional<Contact> contactOpt = contactRepository.findById(id);
        if (contactOpt.isPresent()) {
            Contact contact = contactOpt.get();
            contact.setStatus(status);
            contact.setUpdatedAt(LocalDateTime.now());
            return contactRepository.save(contact);
        }
        throw new RuntimeException("Contact message not found with id: " + id);
    }

    /**
     * Delete contact message
     */
    public void deleteMessage(Long id) {
        if (contactRepository.existsById(id)) {
            contactRepository.deleteById(id);
        } else {
            throw new RuntimeException("Contact message not found with id: " + id);
        }
    }

    /**
     * Get unread messages count
     */
    public long getUnreadMessagesCount() {
        return contactRepository.countByStatus("NEW");
    }

    /**
     * Send email notification to admin
     */
    private void sendEmailNotification(ContactMessage contactMessage) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject("🔔 New Contact Form Submission: " + contactMessage.getSubject());
            message.setText(buildEmailContent(contactMessage));
            message.setFrom(fromEmail); // Use configured Gmail address
            message.setReplyTo(contactMessage.getEmail()); // Set reply-to as sender's email

            mailSender.send(message);
            System.out.println("Email notification sent successfully to: " + adminEmail);

        } catch (Exception e) {
            // Log error but don't throw exception to avoid breaking the main flow
            System.err.println("Failed to send email notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Build email content for notification
     */
    private String buildEmailContent(ContactMessage contactMessage) {
        StringBuilder content = new StringBuilder();
        content.append("🎯 NEW CONTACT FORM SUBMISSION\n");
        content.append("================================\n\n");
        content.append("👤 Name: ").append(contactMessage.getName()).append("\n");
        content.append("📧 Email: ").append(contactMessage.getEmail()).append("\n");
        content.append("📝 Subject: ").append(contactMessage.getSubject()).append("\n\n");
        content.append("💬 Message:\n");
        content.append("---\n");
        content.append(contactMessage.getMessage()).append("\n");
        content.append("---\n\n");
        content.append("⏰ Received: ").append(LocalDateTime.now()).append("\n");
        content.append("🌐 Source: Portfolio Website Contact Form\n\n");
        content.append("💡 Tip: You can reply directly to this email to respond to the sender.\n\n");
        content.append("--\n");
        content.append("Portfolio Website - Automated Notification System");
        
        return content.toString();
    }

    /**
     * Send reply to contact message
     */
    public void sendReply(Long contactId, String replyMessage) {
        Optional<Contact> contactOpt = contactRepository.findById(contactId);
        if (contactOpt.isPresent()) {
            Contact contact = contactOpt.get();
            
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(contact.getEmail());
                message.setSubject("Re: " + contact.getSubject());
                message.setText(buildReplyContent(contact, replyMessage));
                message.setFrom(fromEmail); // Use configured Gmail address

                mailSender.send(message);

                // Update contact status
                contact.setStatus("REPLIED");
                contact.setUpdatedAt(LocalDateTime.now());
                contactRepository.save(contact);

                System.out.println("Reply sent successfully to: " + contact.getEmail());

            } catch (Exception e) {
                throw new RuntimeException("Failed to send reply: " + e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("Contact message not found with id: " + contactId);
        }
    }

    /**
     * Build reply email content
     */
    private String buildReplyContent(Contact contact, String replyMessage) {
        StringBuilder content = new StringBuilder();
        content.append("Hello ").append(contact.getName()).append(",\n\n");
        content.append("Thank you for contacting me through my portfolio website.\n\n");
        content.append(replyMessage).append("\n\n");
        content.append("Original Message:\n");
        content.append("Subject: ").append(contact.getSubject()).append("\n");
        content.append("Message: ").append(contact.getMessage()).append("\n\n");
        content.append("Best regards,\n");
        content.append("Tapas Ranjan Sahoo\n");
        content.append("Full Stack Developer\n");
        content.append("Email: tapasranjanhr@gmail.com\n");
        content.append("Phone: +91 8290684273");
        
        return content.toString();
    }
}