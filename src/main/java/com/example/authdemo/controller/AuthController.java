package com.example.authdemo.controller;



import com.example.authdemo.model.GoogleLoginRequest;
import com.example.authdemo.model.ResetPasswordRequest;
import com.example.authdemo.model.User;

import com.example.authdemo.service.AuthService;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://excel-bulk-mail.onrender.com")
public class AuthController {

    @Autowired
    private AuthService authService;

    
    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verify(@RequestBody User user) {
    	Map<String, String> response = new HashMap<>();
    	try {
    		String mail = authService.verify(user);
    		response.put("email", mail);
    		return ResponseEntity.ok(response);
    	
    	} catch(RuntimeException e) {
		response.put("message", e.getMessage());
		return ResponseEntity.status(409).body(response);
	}
    }
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody User user) {
    	 Map<String, String> response = new HashMap<>();
    	
        User registeredUser = authService.register(user);
 
        response.put("hi",registeredUser.getName().toUpperCase() );
        response.put("Mail",registeredUser.getEmail() );
        response.put("Name",registeredUser.getName() );
        return ResponseEntity.ok(response);
    	}
    
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        String token = authService.authenticate(user);

        Map<String, String> response = new HashMap<>();
        
        if (token != null) {
        	//session.setAttribute("token", token);
            response.put("message", "Login successful");
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);  
        } else {
            response.put("message", "Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);  
        }
    }
    
   
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
        	String email = request.get("email");
            authService.initiatePasswordReset(email);
            response.put("message", "Password reset email sent.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        Map<String, String> response = new HashMap<>();
        if (authService.resetPassword(request.getToken(), request.getNewPassword())) {
            response.put("message", "Password reset successful.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", "Invalid or expired token.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
    	//session.removeAttribute("token"); 
        session.invalidate();
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }
    
    @PostMapping("/profile")
    public ResponseEntity<Map<String, String>> getProfile(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        String token = request.get("token");
        
      
        User user = authService.getUserByToken(token);

        if (user != null) {
        	response.put("Message", "WELCOME "+user.getName().toUpperCase());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", "Invalid or expired token.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
  

    @PostMapping("/google-login")
    public ResponseEntity<Map<String, String>> googleLogin(@RequestBody GoogleLoginRequest request) {
    	String googleToken = request.getGoogleToken();
        String email = request.getEmail();
        String token = authService.verifyGoogleToken(googleToken,email);
        Map<String, String> response = new HashMap<>();
        if (token != null) {
        	//session.setAttribute("token", token);
            response.put("message", "Login successful");
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);  
        } else {
            response.put("message", "Invalid email");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);  
        }
    }
    
    
   
}

