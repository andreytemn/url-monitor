package com.github.andreytemn.monitor.repository;

import com.github.andreytemn.monitor.MonitoringResultTestingUtils;
import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoringResult;
import com.github.andreytemn.monitor.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
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
    public User user;
    public MonitoredEndpoint endpoint;
    @Autowired
    private MonitoringResultRepository monitoringResultRepository;

    @Autowired
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .accessToken(UUID.randomUUID().toString())
                .build());
        endpoint = monitoredEndpointRepository.save(MonitoredEndpoint.builder()
                .name("Google")
                .url("https://www.google.com")
                .monitoredInterval(60)
                .owner(user)
                .lastCheckAt(LocalDateTime.now())
                .build());
    }

    @AfterEach
    void cleanUp() {
        monitoringResultRepository.deleteAll();
        monitoredEndpointRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testSaveAndFind() {
        MonitoringResult monitoringResult = MonitoringResult.builder()
                .checkDate(LocalDateTime.now())
                .httpStatusCode(HttpStatus.OK.value())
                .payload("payload")
                .monitoredEndpoint(endpoint).build();

        MonitoringResult saved = monitoringResultRepository.save(monitoringResult);
        assertEquals(monitoringResult.getHttpStatusCode(), saved.getHttpStatusCode());
        assertEquals(endpoint.getId(), saved.getMonitoredEndpoint().getId());
    }

    @Test
    void testFindTop10ByMonitoredEndpointOrderByCheckDateDesc() {
        monitoringResultRepository.saveAll(MonitoringResultTestingUtils.createMonitoringResults(20, endpoint));

        List<MonitoringResult> actualResults =
                monitoringResultRepository.findTop10ByMonitoredEndpointOrderByCheckDateDesc(endpoint);

        assertEquals(10, actualResults.size());
    }

    @Test
    void testReturnsEmptyListIfNoneResultsFound() {
        List<MonitoringResult> actualResults =
                monitoringResultRepository.findTop10ByMonitoredEndpointOrderByCheckDateDesc(endpoint);
        assertTrue(actualResults.isEmpty());
    }
}