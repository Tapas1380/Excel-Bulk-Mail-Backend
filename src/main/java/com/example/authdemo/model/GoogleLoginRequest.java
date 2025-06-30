package com.example.authdemo.model;

public class GoogleLoginRequest {
    private String googleToken;
    private String email;

    // Getters and setters
    public String getGoogleToken() {
        return googleToken;
    }

    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}