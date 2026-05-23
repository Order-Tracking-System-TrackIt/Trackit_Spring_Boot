package com.trackit.auth.config;

import com.trackit.auth.entity.Authentication;
import com.trackit.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // ✅ Create admin if doesn't exist
        if (userRepository.findByEmail("admin@trackit.com") == null) {
            Authentication admin = new Authentication();
            admin.setName("Admin User");
            admin.setEmail("admin@trackit.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("admin");
            userRepository.save(admin);
            System.out.println("Admin account created: admin@trackit.com / admin123");
        }

        // ✅ Create support if doesn't exist
        if (userRepository.findByEmail("support@trackit.com") == null) {
            Authentication support = new Authentication();
            support.setName("Support Team");
            support.setEmail("support@trackit.com");
            support.setPassword(passwordEncoder.encode("support123"));
            support.setRole("support");
            userRepository.save(support);
            System.out.println("Support account created: support@trackit.com / support123");
        }
    }
}