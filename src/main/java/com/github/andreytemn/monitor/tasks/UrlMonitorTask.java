package com.github.andreytemn.monitor.tasks;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoringResult;
import com.github.andreytemn.monitor.repository.MonitoredEndpointRepository;
import com.github.andreytemn.monitor.repository.MonitoringResultRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UrlMonitorTask {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MonitoredEndpointRepository endpointRepository;

    @Autowired
    private MonitoringResultRepository resultRepository;

    private final Map<UUID, MonitoredEndpoint> endpoints = new ConcurrentHashMap<>();

    @PostConstruct
    public void initEndpoints() {
        endpointRepository.findAll().forEach(it -> endpoints.put(it.getId(), it));
    }

    @Scheduled(fixedDelayString = "${monitoring.interval}")
    public void monitorEndpoints() {
        for (MonitoredEndpoint endpoint : endpoints.values()) {
            if (shouldCheckEndpoint(endpoint)) {
                MonitoringResult monitoringResult = toMonitoringResult(restTemplate.getForEntity(endpoint.getUrl(),
                        String.class), endpoint);
                resultRepository.save(monitoringResult);
                endpointRepository.save(endpoint.toBuilder().lastCheckAt(LocalDateTime.now()).build());
            }
        }
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
