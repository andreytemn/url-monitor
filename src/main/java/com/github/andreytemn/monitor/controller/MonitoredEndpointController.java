package com.github.andreytemn.monitor.controller;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoredEndpointRequest;
import com.github.andreytemn.monitor.service.MonitoredEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/endpoints")
public class MonitoredEndpointController {

    @Autowired
    private MonitoredEndpointService monitoredEndpointService;

    @GetMapping
    public List<MonitoredEndpoint> getAllMonitoredEndpoints() {
        return monitoredEndpointService.findAll();
    }

    @GetMapping("/{id}")
    public MonitoredEndpoint getMonitoredEndpointById(@PathVariable UUID id) {
        return monitoredEndpointService.findById(id);
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MonitoredEndpoint createMonitoredEndpoint(@RequestBody MonitoredEndpointRequest monitoredEndpoint) {
        return monitoredEndpointService.save(monitoredEndpoint);
    }

    @PutMapping("/{id}")
    public MonitoredEndpoint updateMonitoredEndpoint(@PathVariable UUID id, @RequestBody MonitoredEndpointRequest monitoredEndpoint) {
        return monitoredEndpointService.updateMonitoredEndpoint(id, monitoredEndpoint);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMonitoredEndpoint(@PathVariable UUID id) {
        monitoredEndpointService.delete(id);
    }
}