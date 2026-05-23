package com.trackit.auth.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.trackit.auth.entity.Authentication;
import com.trackit.auth.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
    RequestMethod.GET, 
    RequestMethod.POST, 
    RequestMethod.PUT, 
    RequestMethod.DELETE, 
    RequestMethod.OPTIONS
})
@RequiredArgsConstructor
@Slf4j // ✅ This enables logging
public class AuthController {
    
    private final AuthService authService;

    // ✅ TEST ENDPOINT
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        log.info("✅ Test endpoint hit");
        return ResponseEntity.ok(Map.of(
            "message", "Auth API is working!", 
            "timestamp", System.currentTimeMillis()
        ));
    }

    // ✅ REGISTER USER
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Authentication user) {
        try {
            log.info("✅ Register request for email: {}", user.getEmail());
            Authentication savedUser = authService.register(user);
            savedUser.setPassword(null); // Don't send password back
            log.info("✅ User registered successfully: {}", savedUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (RuntimeException e) {
            log.error("❌ Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Authentication request) {
        try {
            log.info("✅ Login attempt for email: {}", request.getEmail());
            
            Authentication user = authService.getUserByEmail(request.getEmail());

            if (user == null) {
                log.warn("❌ User not found: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid credentials"));
            }

            boolean isMatch = authService.checkPassword(
                    request.getPassword(),
                    user.getPassword()
            );

            if (!isMatch) {
                log.warn("❌ Invalid password for user: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid credentials"));
            }

            // Don't send password back to client
            user.setPassword(null);
            log.info("✅ Login successful for user: {}", user.getEmail());
            
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer-token-placeholder") // Add real JWT here
                    .body(user);
                    
        } catch (Exception e) {
            log.error("❌ Login error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Login failed"));
        }
    }

    // FORGOT PASSWORD
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            log.info("✅ Forgot password request received for email: {}", email);
            
            if (email == null || email.trim().isEmpty()) {
                log.warn("❌ Empty email in forgot password request");
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email is required"));
            }
            
            authService.forgotPassword(email.trim());
            log.info("✅ Forgot password processed for email: {}", email);
            
            return ResponseEntity.ok()
                    .body(Map.of(
                        "success", true,
                        "message", "If the email exists in our system, a new password has been sent."
                    ));
                    
        } catch (Exception e) {
            log.error("❌ Forgot password error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false,
                        "message", "Failed to process request. Please try again later."
                    ));
        }
    }

    // ✅ GET USER BY EMAIL
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            log.info("✅ Get user by email request: {}", email);
            Authentication user = authService.getUserByEmail(email);
            
            if (user == null) {
                log.warn("❌ User not found: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found"));
            }
            
            user.setPassword(null); // Don't send password
            log.info("✅ User found: {}", email);
            return ResponseEntity.ok(user);
            
        } catch (Exception e) {
            log.error("❌ Error getting user by email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to retrieve user"));
        }
    }

    // ✅ UPDATE USER BY ID
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody Authentication user, @PathVariable Long id) {
        try {
            log.info("✅ Update user request for id: {}", id);
            Authentication updatedUser = authService.updateUserById(user, id);
            updatedUser.setPassword(null); // Don't send password back
            log.info("✅ User updated successfully: {}", id);
            return ResponseEntity.ok(updatedUser);
            
        } catch (RuntimeException e) {
            log.error("❌ Update user failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}