package com.github.andreytemn.monitor.controller;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoredEndpointRequest;
import com.github.andreytemn.monitor.service.MonitoredEndpointService;
import com.github.andreytemn.monitor.service.UserService;
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

    @Autowired
    private UserService userService;

    @GetMapping
    public List<MonitoredEndpoint> getAllMonitoredEndpoints(@RequestHeader("AccessToken") String accessToken) {
        return monitoredEndpointService.findAll(userService.validateAccessToken(accessToken));
    }

    @GetMapping("/{id}")
    public MonitoredEndpoint getMonitoredEndpointById(@PathVariable UUID id,
                                                      @RequestHeader("AccessToken") String accessToken) {
        return monitoredEndpointService.findById(id, userService.validateAccessToken(accessToken));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MonitoredEndpoint createMonitoredEndpoint(@RequestBody MonitoredEndpointRequest monitoredEndpoint,
                                                     @RequestHeader("AccessToken") String accessToken) {
        return monitoredEndpointService.save(monitoredEndpoint, userService.validateAccessToken(accessToken));
    }

    @PutMapping("/{id}")
    public MonitoredEndpoint updateMonitoredEndpoint(@PathVariable UUID id,
                                                     @RequestBody MonitoredEndpointRequest monitoredEndpoint,
                                                     @RequestHeader("AccessToken") String accessToken) {
        return monitoredEndpointService.updateMonitoredEndpoint(id, monitoredEndpoint,
                userService.validateAccessToken(accessToken));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMonitoredEndpoint(@PathVariable UUID id, @RequestHeader("AccessToken") String accessToken) {
        monitoredEndpointService.delete(id, userService.validateAccessToken(accessToken));
    }
}