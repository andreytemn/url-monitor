package com.github.andreytemn.monitor.service;


import com.github.andreytemn.monitor.model.User;
import com.github.andreytemn.monitor.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test for {@link UserSeeder}. Asserts the default users of the service.
 */
@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
class UserSeederTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSeeder userSeeder;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void testSeedUsers() {
        userSeeder.run();
        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());

        User user1 = users.stream()
                .filter(user -> user.getUsername().equals("Applifting"))
                .findFirst()
                .orElse(null);
        assertNotNull(user1);
        assertNotNull(user1.getId());
        assertEquals("Applifting", user1.getUsername());
        assertEquals("info@applifting.cz", user1.getEmail());
        assertEquals("93f39e2f-80de-4033-99ee-249d92736a25", user1.getAccessToken());

        User user2 = users.stream()
                .filter(user -> user.getUsername().equals("Batman"))
                .findFirst()
                .orElse(null);
        assertNotNull(user2);
        assertNotNull(user2.getId());
        assertEquals("Batman", user2.getUsername());
        assertEquals("batman@example.com", user2.getEmail());
        assertEquals("dcb20f8a-5657-4f1b-9f7f-ce65739b359e", user2.getAccessToken());
    }
}