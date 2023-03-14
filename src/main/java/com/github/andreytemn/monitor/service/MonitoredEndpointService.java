package com.github.andreytemn.monitor.service;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoredEndpointRequest;
import com.github.andreytemn.monitor.model.MonitoringResult;
import com.github.andreytemn.monitor.model.User;
import com.github.andreytemn.monitor.repository.MonitoredEndpointRepository;
import com.github.andreytemn.monitor.repository.MonitoringResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MonitoredEndpointService {

    @Autowired
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @Autowired
    private MonitoringResultRepository monitoringResultRepository;

    public MonitoredEndpoint save(MonitoredEndpointRequest monitoredEndpoint, User owner) {
        return monitoredEndpointRepository.save(MonitoredEndpoint.builder()
                .name(monitoredEndpoint.name())
                .url(monitoredEndpoint.url())
                .monitoredInterval(monitoredEndpoint.monitoredInterval())
                .lastCheckAt(LocalDateTime.now())
                .owner(owner).build());
    }

    public MonitoredEndpoint findById(UUID id, User owner) {
        MonitoredEndpoint endpoint =
                monitoredEndpointRepository.findById(id).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MonitoredEndpoint not found"));
        if (!endpoint.getOwner().equals(owner)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "MonitoredEndpoint does not belong to this user");
        }
        return endpoint;
    }

    public List<MonitoredEndpoint> findAll(User owner) {
        return monitoredEndpointRepository.findByOwner(owner);
    }

    public void delete(UUID id, User owner) {
        monitoredEndpointRepository.delete(findById(id, owner));
    }

    public MonitoredEndpoint updateMonitoredEndpoint(UUID id, MonitoredEndpointRequest monitoredEndpoint, User owner) {
        MonitoredEndpoint existingEndpoint = findById(id, owner).toBuilder()
                .name(monitoredEndpoint.name())
                .url(monitoredEndpoint.url())
                .monitoredInterval(monitoredEndpoint.monitoredInterval())
                .build();

        return monitoredEndpointRepository.save(existingEndpoint);
    }

    public List<MonitoringResult> getMonitoringResults(MonitoredEndpoint endpoint) {
        return monitoringResultRepository.findTop10ByMonitoredEndpointOrderByCheckDateDesc(endpoint);
    }
}