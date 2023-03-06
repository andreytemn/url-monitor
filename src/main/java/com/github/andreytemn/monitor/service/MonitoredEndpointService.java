package com.github.andreytemn.monitor.service;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoredEndpointRequest;
import com.github.andreytemn.monitor.repository.MonitoredEndpointRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class MonitoredEndpointService {

    @Autowired
    private MonitoredEndpointRepository monitoredEndpointRepository;

    public MonitoredEndpoint save(MonitoredEndpointRequest monitoredEndpoint) {

        return monitoredEndpointRepository.save(MonitoredEndpoint.builder().name(monitoredEndpoint.name()).url(monitoredEndpoint.url()).monitoredInterval(monitoredEndpoint.monitoredInterval()).build());
    }

    public MonitoredEndpoint findById(UUID id) {
        return monitoredEndpointRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MonitoredEndpoint not found"));
    }

    public List<MonitoredEndpoint> findAll() {
        return monitoredEndpointRepository.findAll();
    }

    public void delete(UUID id) {
        monitoredEndpointRepository.delete(findById(id));
    }

    public MonitoredEndpoint updateMonitoredEndpoint(UUID id, MonitoredEndpointRequest monitoredEndpoint) {
        MonitoredEndpoint existingEndpoint = monitoredEndpointRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No monitored endpoint with id " + id))
                .toBuilder()
                .name(monitoredEndpoint.name())
                .url(monitoredEndpoint.url())
                .monitoredInterval(monitoredEndpoint.monitoredInterval())
                .build();

        return monitoredEndpointRepository.save(existingEndpoint);
    }
}