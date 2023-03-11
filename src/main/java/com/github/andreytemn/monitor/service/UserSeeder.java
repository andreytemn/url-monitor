package com.github.andreytemn.monitor.service;

import com.github.andreytemn.monitor.model.User;
import com.github.andreytemn.monitor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Provides the default users for the service.
 */
@Component
public class UserSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        User applifting = User.builder().username("Applifting").email("info@applifting.cz").accessToken("93f39e2f-80de-4033-99ee-249d92736a25").build();
        User batman = User.builder().username("Batman").email("batman@example.com").accessToken("dcb20f8a-5657-4f1b-9f7f-ce65739b359e").build();
        userRepository.save(applifting);
        userRepository.save(batman);
    }
}