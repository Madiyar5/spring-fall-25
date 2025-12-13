package org.example.sis9.config;

import org.example.sis9.model.User;
import org.example.sis9.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Создать тестового пользователя
        if (!userRepository.existsByUsername("user")) {
            User user = new User("user", passwordEncoder.encode("password"), "user@example.com");
            user.setRoles(Set.of("USER"));
            userRepository.save(user);
            System.out.println("Test user created: username=user, password=password");
        }

        // Создать тестового админа
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", passwordEncoder.encode("admin"), "admin@example.com");
            admin.setRoles(Set.of("ADMIN", "USER"));
            userRepository.save(admin);
            System.out.println("Test admin created: username=admin, password=admin");
        }
    }
}