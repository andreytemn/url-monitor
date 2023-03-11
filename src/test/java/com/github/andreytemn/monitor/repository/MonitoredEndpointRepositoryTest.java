package com.github.andreytemn.monitor.repository;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * A test for {@link MonitoredEndpointRepository}
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class MonitoredEndpointRepositoryTest {

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("monitoring_db")
            .withUsername("myuser")
            .withPassword("urlMonitorUserPass");

    @Autowired
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByOwner() {
        User user = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .accessToken(UUID.randomUUID().toString())
                .build();
        userRepository.save(user);
        MonitoredEndpoint endpoint = MonitoredEndpoint.builder()
                .name("Google")
                .url("https://www.google.com")
                .monitoredInterval(60)
                .owner(user)
                .lastCheckAt(LocalDateTime.now())
                .build();
        monitoredEndpointRepository.save(endpoint);

        List<MonitoredEndpoint> endpoints = monitoredEndpointRepository.findByOwner(user);
        assertThat(endpoints).hasSize(1);
        MonitoredEndpoint foundEndpoint = endpoints.get(0);
        assertEquals(endpoint.getName(), foundEndpoint.getName());
        assertEquals(endpoint.getUrl(), foundEndpoint.getUrl());
        assertEquals(endpoint.getMonitoredInterval(), foundEndpoint.getMonitoredInterval());
        assertEquals(endpoint.getOwner().getAccessToken(), foundEndpoint.getOwner().getAccessToken());
        assertNotNull(endpoint.getUpdatedAt());
        assertNotNull(endpoint.getLastCheckAt());
        assertNotNull(endpoint.getCreatedAt());
    }

    @Test
    void testFindByOwnerWithNonExistentUser() {
        User anotherUser = User.builder()
                .username("nonexistentuser")
                .email("nonexistentuser@example.com")
                .accessToken(UUID.randomUUID().toString())
                .build();

        userRepository.save(anotherUser);

        assertThat(monitoredEndpointRepository.findByOwner(anotherUser)).isEmpty();
    }
}