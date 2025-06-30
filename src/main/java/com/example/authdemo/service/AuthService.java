package com.example.authdemo.service;

import com.example.authdemo.jwt.JwtUtil;
import com.example.authdemo.model.User;
import com.example.authdemo.repository.UserRepository;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.auth.oauth2.TokenVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // Use the PasswordEncoder bean defined in SecurityConfig

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EmailService emailService;
    
    @Transactional
    public User register(User user) {
        // Check if the user already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
        	 
            throw new RuntimeException("User already exists!");
        }

        // Encrypt password and save the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // Use the injected PasswordEncoder
        return userRepository.save(user);
    }
    public String verify(User user) {
        // Check if the user already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
        	 
            throw new RuntimeException("User already exists!");
        }
        return user.getEmail();
    }
    private Map<String, String> profile = new HashMap<>();
    public String authenticate(User user) {
        
        Optional<User> existingUserOpt = userRepository.findByEmail(user.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
    
            if(passwordEncoder.matches(user.getPassword(), existingUser.getPassword())){
            	String token = jwtUtil.generateToken(existingUser.getEmail());
            	profile.put(token, existingUser.getEmail());
            	return token;
            }
        }
        return null;
    }
    
    
    
    //authenticate by google
    // Your Google OAuth2 Client ID (replace with actual client ID)
    private static final String CLIENT_ID = "530072404641-g0pceu5ggpqgf762h2e4hnel78e3ackb.apps.googleusercontent.com";

    // Method to verify the Google ID token
    public String verifyGoogleToken(String googleToken,String email) {
        try {
            // Create an IdTokenVerifier with your Client ID
            TokenVerifier verifier = TokenVerifier.newBuilder()
                    .setAudience(CLIENT_ID)
                    .build();

            // Verify the ID token
            JsonWebSignature tokens = verifier.verify(googleToken);
            if (tokens != null) {
                System.out.println("Token verified! User ID: " + tokens.getPayload().getSubject());
                Optional<User> user = userRepository.findByEmail(email);
                if (user.isPresent()) {
                	User existingUser = user.get();
                
                    // Generate and return a JWT for the user
                    String token = jwtUtil.generateToken(existingUser.getEmail());
                    profile.put(token,email);
                    return token;
                }
                
            }
        } catch (Exception e) {
            System.err.println("Token verification failed: " + e.getMessage());
        }
        return null;
    }
    
        
        
        
        
        
    private Map<String, String> passwordResetTokenMap = new HashMap<>();
    
    public void initiatePasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User with this email does not exist.");
        }

        String token = UUID.randomUUID().toString();
        // Save token to a map or a database with an expiration time
        passwordResetTokenMap.put(token, email);

        // Send token in a password reset email
        emailService.sendPasswordResetEmail(email, token);
    }

    public boolean resetPassword(String token, String newPassword) {
        String email = passwordResetTokenMap.get(token);
        if (email == null) {
            return false;
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            passwordResetTokenMap.remove(token);
            return true;
        }

        return false;
    
    }
    
   

    public User getUserByToken(String token) {
        
    	String email = profile.get(token);
        return userRepository.findByEmail(email).orElse(null);
    }
    
  
    

    
}
