package com.github.andreytemn.monitor.service;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoredEndpointRequest;
import com.github.andreytemn.monitor.model.User;
import com.github.andreytemn.monitor.repository.MonitoredEndpointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MonitoredEndpointServiceTest {

    @Autowired
    private MonitoredEndpointService monitoredEndpointService;

    @Autowired
    private MonitoredEndpointRepository monitoredEndpointRepository;

    private UUID testEndpointId;

    @BeforeEach
    public void setup() {
        monitoredEndpointRepository.deleteAll();

        // Create a test endpoint
        MonitoredEndpoint testEndpoint = monitoredEndpointRepository.save(MonitoredEndpoint.builder()
                .name("Test Endpoint")
                .url("http://example.com")
                .monitoredInterval(60)
                .owner(User.builder().email("user@email.com").username("user").accessToken("token").build())
                .build());

        testEndpointId = testEndpoint.getId();
    }

    @Test
    void testSaveEndpoint() {
        MonitoredEndpointRequest request = new MonitoredEndpointRequest("New Endpoint", "http://example.org", 120);

        MonitoredEndpoint savedEndpoint = monitoredEndpointService.save(request);

        assertNotNull(savedEndpoint.getId());
        assertEquals(request.name(), savedEndpoint.getName());
        assertEquals(request.url(), savedEndpoint.getUrl());
        assertEquals(request.monitoredInterval(), savedEndpoint.getMonitoredInterval());
    }

    @Test
    void testFindById() {
        MonitoredEndpoint endpoint = monitoredEndpointService.findById(testEndpointId);

        assertNotNull(endpoint);
        assertEquals(testEndpointId, endpoint.getId());
        assertEquals("Test Endpoint", endpoint.getName());
        assertEquals("http://example.com", endpoint.getUrl());
        assertEquals(60, endpoint.getMonitoredInterval());
    }

    @Test
    void testFindAll() {
        List<MonitoredEndpoint> endpoints = monitoredEndpointService.findAll();

        assertNotNull(endpoints);
        assertEquals(1, endpoints.size());
        assertEquals(testEndpointId, endpoints.get(0).getId());
        assertEquals("Test Endpoint", endpoints.get(0).getName());
        assertEquals("http://example.com", endpoints.get(0).getUrl());
        assertEquals(60, endpoints.get(0).getMonitoredInterval());
    }

    @Test
    void testDelete() {
        monitoredEndpointService.delete(testEndpointId);

        assertFalse(monitoredEndpointRepository.existsById(testEndpointId));
    }

    @Test
    void testUpdateMonitoredEndpoint() {
        MonitoredEndpointRequest request = new MonitoredEndpointRequest("Updated Endpoint", "http://example.net", 90);

        MonitoredEndpoint updatedEndpoint = monitoredEndpointService.updateMonitoredEndpoint(testEndpointId, request);

        assertNotNull(updatedEndpoint.getId());
        assertEquals(request.name(), updatedEndpoint.getName());
        assertEquals(request.url(), updatedEndpoint.getUrl());
        assertEquals(request.monitoredInterval(), updatedEndpoint.getMonitoredInterval());

        MonitoredEndpoint retrievedEndpoint = monitoredEndpointRepository.findById(testEndpointId).orElseThrow(AssertionError::new);
        assertEquals(request.name(), retrievedEndpoint.getName());
        assertEquals(request.url(), retrievedEndpoint.getUrl());
        assertEquals(request.monitoredInterval(), retrievedEndpoint.getMonitoredInterval());
    }
}