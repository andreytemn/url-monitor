package com.github.andreytemn.monitor.repository;

import com.github.andreytemn.monitor.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A test for {@link UserRepository}
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("monitoring_db")
            .withUsername("myuser")
            .withPassword("urlMonitorUserPass");

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void testFindByAccessToken() {
        User user = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .accessToken(UUID.randomUUID().toString())
                .build();
        userRepository.save(user);

        User foundUser = userRepository.findByAccessToken(user.getAccessToken());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(foundUser.getAccessToken()).isEqualTo(user.getAccessToken());
    }

    @Test
    void testFindByAccessTokenReturnsNullWhenUserNotFound() {
        assertThat(userRepository.findByAccessToken(UUID.randomUUID().toString())).isNull();
    }
}