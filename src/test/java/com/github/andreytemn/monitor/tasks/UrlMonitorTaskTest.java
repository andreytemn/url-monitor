package com.github.andreytemn.monitor.tasks;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoringResult;
import com.github.andreytemn.monitor.repository.MonitoredEndpointRepository;
import com.github.andreytemn.monitor.repository.MonitoringResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Test for {@link UrlMonitorTask}
 */
@ExtendWith(MockitoExtension.class)
class UrlMonitorTaskTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MonitoredEndpointRepository endpointRepository;

    @Mock
    private MonitoringResultRepository resultRepository;

    @InjectMocks
    private UrlMonitorTask urlMonitorTask;

    private MonitoredEndpoint endpoint1;
    private MonitoredEndpoint endpoint2;
    private final Map<UUID, MonitoredEndpoint> endpoints = new HashMap<>();

    @BeforeEach
    public void setUp() {
        endpoint1 = MonitoredEndpoint.builder()
                .id(UUID.randomUUID())
                .name("Example")
                .url("https://www.example.com")
                .monitoredInterval(60)
                .createdAt(LocalDateTime.now())
                .build();
        endpoint2 = MonitoredEndpoint.builder()
                .id(UUID.randomUUID())
                .name("Google")
                .url("https://www.google.com")
                .monitoredInterval(30)
                .createdAt(LocalDateTime.now())
                .build();

        endpoints.put(endpoint1.getId(), endpoint1);
        endpoints.put(endpoint2.getId(), endpoint2);

        when(endpointRepository.findAll()).thenReturn(new ArrayList<>(endpoints.values()));
    }

    @Test
    void testMonitorEndpoints() {
        ResponseEntity<String> responseEntity1 = new ResponseEntity<>("Example response", HttpStatus.OK);
        ResponseEntity<String> responseEntity2 = new ResponseEntity<>("Google response", HttpStatus.OK);

        when(restTemplate.getForEntity(endpoint1.getUrl(), String.class)).thenReturn(responseEntity1);
        when(restTemplate.getForEntity(endpoint2.getUrl(), String.class)).thenReturn(responseEntity2);

        urlMonitorTask.initEndpoints();
        urlMonitorTask.monitorEndpoints();

        verify(resultRepository, times(2)).save(any(MonitoringResult.class));
        verify(endpointRepository, times(2)).save(any(MonitoredEndpoint.class));
    }
}