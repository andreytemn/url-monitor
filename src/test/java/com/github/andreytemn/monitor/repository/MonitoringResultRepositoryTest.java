package com.github.andreytemn.monitor.repository;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoringResult;
import com.github.andreytemn.monitor.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for {@link MonitoredEndpointRepository}
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class MonitoringResultRepositoryTest {

    @Autowired
    private MonitoringResultRepository monitoringResultRepository;

    @Autowired
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFind() {
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

        MonitoringResult monitoringResult = MonitoringResult.builder()
                .id(UUID.randomUUID())
                .checkDate(LocalDateTime.now())
                .httpStatusCode(HttpStatus.OK.value())
                .monitoredEndpoint(endpoint).build();

        monitoringResultRepository.save(monitoringResult);

        Optional<MonitoringResult> savedResult = monitoringResultRepository.findById(monitoringResult.getId());

        assertTrue(savedResult.isPresent());
        assertEquals(monitoringResult, savedResult.get());
    }
}