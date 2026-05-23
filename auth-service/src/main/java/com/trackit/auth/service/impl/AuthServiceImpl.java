package com.trackit.auth.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trackit.auth.entity.Authentication;
import com.trackit.auth.repository.UserRepository;
import com.trackit.auth.service.AuthService;
import com.trackit.auth.service.EmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    

    @Override
    @Transactional
    public void forgotPassword(String email) {

        Optional<Authentication> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            Authentication user = userOpt.get();

        
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);

            user.setPassword(passwordEncoder.encode(tempPassword));
            userRepository.save(user);

          
            String emailBody = "Hello " + (user.getName() != null ? user.getName() : "User") + ",\n\n" +
                    "A password reset was requested for your TrackIt account.\n" +
                    "Your new temporary password is: " + tempPassword + "\n\n" +
                    "Please log in and change your password immediately for security.";

            emailService.sendSimpleEmail(user.getEmail(), "Your New TrackIt Password", emailBody);
        }
      
    }

 
    @Override
    public Authentication register(Authentication user) {
        
     
        Optional<Authentication> existingUserOpt = userRepository.findByEmail(user.getEmail());
        
        if (existingUserOpt.isPresent()) {
            Authentication existingUser = existingUserOpt.get();
            
            
            if ("admin".equalsIgnoreCase(existingUser.getRole()) || 
                "support".equalsIgnoreCase(existingUser.getRole())) {
                throw new RuntimeException("Account already exists. Please login instead.");
            }
            
        
            throw new RuntimeException("Email already registered. Please login.");
        }

       

    
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }


    @Override
    public Authentication updateUserById(Authentication user, Long id) {

        Authentication existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(existingUser);
    }


    @Override
    public Authentication getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElse(null);
    }


    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {

        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
