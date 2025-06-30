package com.example.authdemo.dto;

public class EmailRequest {
	private String toEmail;
    private String subject;
    private String body;
    private boolean isHtml;

    // Constructors
    public EmailRequest() {}

    public EmailRequest(String toEmail, String subject, String body, boolean isHtml) {
        this.toEmail = toEmail;
        this.subject = subject;
        this.body = body;
        this.isHtml = isHtml;
    }

    // Getters and Setters
    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isHtml() {
        return isHtml;
    }

    public void setHtml(boolean html) {
        isHtml = html;
    }

}
