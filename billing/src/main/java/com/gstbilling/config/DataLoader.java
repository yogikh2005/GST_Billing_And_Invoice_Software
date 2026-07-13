package com.gstbilling.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.gstbilling.dao.UserRepository;
import com.gstbilling.models.Role;
import com.gstbilling.models.User;

@Component
public class DataLoader implements CommandLineRunner {
	
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {
        if (userRepository.findByRole(Role.ROLE_OWNER).isEmpty()) {
            User owner = User.builder()
                    .username("yogikh2007")
                    .email("yogikh2018@gmail.com")
                    .password(passwordEncoder.encode("123"))
                    .role(Role.ROLE_OWNER)
                    .build();

            userRepository.save(owner);
            System.out.println("Default owner created.");
        }
    }
}
