package com.example.authdemo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authdemo.dto.ContactMessage;
import com.example.authdemo.dto.EmailRequest;
import com.example.authdemo.service.ContactService;
import com.example.authdemo.service.EmailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = {"http://localhost:4200", "https://tapas-ten.vercel.app/"}, 
allowedHeaders = "*",
allowCredentials = "true")
public class EmailController {


    @Autowired
    private EmailService emailService;
    
    @Autowired
    private ContactService contactService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) {
        try {
            if (emailRequest.isHtml()) {
                emailService.sendHtmlEmail(
                    emailRequest.getToEmail(),
                    emailRequest.getSubject(),
                    emailRequest.getBody()
                );
            } else {
                emailService.sendSimpleEmail(
                    emailRequest.getToEmail(),
                    emailRequest.getSubject(),
                    emailRequest.getBody()
                );
            }
            return ResponseEntity.ok("Email sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/send-bulk")
    public ResponseEntity<String> sendBulkEmails(@RequestBody EmailRequest[] emailRequests) {
        try {
            int successCount = 0;
            int failCount = 0;
            
            for (EmailRequest request : emailRequests) {
                try {
                    if (request.isHtml()) {
                        emailService.sendHtmlEmail(
                            request.getToEmail(),
                            request.getSubject(),
                            request.getBody()
                        );
                    } else {
                        emailService.sendSimpleEmail(
                            request.getToEmail(),
                            request.getSubject(),
                            request.getBody()
                        );
                    }
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    System.err.println("Failed to send email to " + request.getToEmail() + ": " + e.getMessage());
                }
            }
            
            return ResponseEntity.ok(String.format(
                "Bulk email completed. Success: %d, Failed: %d", 
                successCount, failCount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to send bulk emails: " + e.getMessage());
        }
    }
   
    @PostMapping("/contact")
    public ResponseEntity<Map<String, Object>> sendMessage(@Valid @RequestBody ContactMessage contactMessage) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Save the message to database or send email
            contactService.saveMessage(contactMessage);
            
            response.put("status", "success");
            response.put("message", "Message sent successfully!");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to send message. Please try again.");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
}
