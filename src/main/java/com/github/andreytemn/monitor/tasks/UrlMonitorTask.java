package com.github.andreytemn.monitor.tasks;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoringResult;
import com.github.andreytemn.monitor.repository.MonitoredEndpointRepository;
import com.github.andreytemn.monitor.repository.MonitoringResultRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Applies endpoint monitoring. The interval of running the task is one second, and it checks if the specified interval
 * for the endpoint has already passed. The last checked date of the endpoint is updated accordingly.
 */
@Component
@Slf4j
public class UrlMonitorTask {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MonitoredEndpointRepository endpointRepository;

    @Autowired
    private MonitoringResultRepository resultRepository;

    private List<MonitoredEndpoint> endpoints = new ArrayList<>();

    /**
     * Init the collection of endpoints to monitor with the content of the according db table.
     */
    @PostConstruct
    public void initEndpoints() {
        endpoints = endpointRepository.findAll();
    }

    /**
     * Find the endpoint which monitoring interval has passed, commit the monitoring and note down the result.
     */
    @Scheduled(fixedDelayString = "${monitoring.interval}")
    public void monitorEndpoints() {
        log.debug("Attempting monitoring");
        endpoints.stream().filter(this::shouldCheckEndpoint).forEach(this::monitor);
    }

    private void monitor(MonitoredEndpoint endpoint) {
        log.debug("Monitoring {}", endpoint);
        MonitoringResult monitoringResult =
                toMonitoringResult(queryEndpoint(endpoint), endpoint);
        resultRepository.save(monitoringResult);
        endpointRepository.save(updateLastCheckData(endpoint));
    }

    private static MonitoredEndpoint updateLastCheckData(MonitoredEndpoint endpoint) {
        return endpoint.toBuilder().lastCheckAt(LocalDateTime.now()).build();
    }

    private ResponseEntity<String> queryEndpoint(MonitoredEndpoint endpoint) {
        return restTemplate.getForEntity(endpoint.getUrl(), String.class);
    }

    private MonitoringResult toMonitoringResult(ResponseEntity<String> response, MonitoredEndpoint endpoint) {
        return MonitoringResult.builder()
                .checkDate(LocalDateTime.now())
                .httpStatusCode(response.getStatusCode().value())
                .payload(Optional.ofNullable(response.getBody()).map(Object::toString).orElse(""))
                .monitoredEndpoint(endpoint)
                .build();
    }

    private boolean shouldCheckEndpoint(MonitoredEndpoint endpoint) {
        return endpoint.getLastCheckAt() == null
                || endpoint.getLastCheckAt().plusSeconds(endpoint.getMonitoredInterval()).isBefore(LocalDateTime.now());
    }
}
