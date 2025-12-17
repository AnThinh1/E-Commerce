package com.codegym.ecommercemanage.config;

import com.codegym.ecommercemanage.model.Role;
import com.codegym.ecommercemanage.model.User;
import com.codegym.ecommercemanage.repository.RoleRepository;
import com.codegym.ecommercemanage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        Role adminRole = roleRepo.findByName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = roleRepo.save(new Role(null, "ROLE_ADMIN"));
        }

        if (!userRepo.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("123456"));
            admin.setFullName("System Admin");
            admin.getRoles().add(adminRole);
            userRepo.save(admin);
        }
    }
}
