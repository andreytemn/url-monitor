package com.github.andreytemn.monitor.service;

import com.github.andreytemn.monitor.MonitoringResultTestingUtils;
import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoredEndpointRequest;
import com.github.andreytemn.monitor.model.MonitoringResult;
import com.github.andreytemn.monitor.model.User;
import com.github.andreytemn.monitor.repository.MonitoredEndpointRepository;
import com.github.andreytemn.monitor.repository.MonitoringResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link MonitoredEndpointService}
 */
@ExtendWith(MockitoExtension.class)
class MonitoredEndpointServiceTest {

    private static final User OWNER =
            User.builder().email("user@email.com").username("user").accessToken("token").build();

    private static final MonitoredEndpoint ENDPOINT = MonitoredEndpoint.builder()
            .id(UUID.randomUUID())
            .name("Test Endpoint")
            .url("http://example.com")
            .monitoredInterval(60)
            .lastCheckAt(LocalDateTime.now())
            .owner(OWNER)
            .build();

    private static final UUID ID = ENDPOINT.getId();

    @Mock
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @Mock
    private MonitoringResultRepository monitoringResultRepository;

    @InjectMocks
    private MonitoredEndpointService monitoredEndpointService;

    @Test
    void testSaveEndpoint() {
        MonitoredEndpointRequest request = new MonitoredEndpointRequest("Test Endpoint", "http://example.com", 60);

        when(monitoredEndpointRepository.save(any(MonitoredEndpoint.class))).thenReturn(ENDPOINT);
        MonitoredEndpoint savedEndpoint = monitoredEndpointService.save(request, OWNER);

        assertNotNull(savedEndpoint.getId());
        assertEquals(request.name(), savedEndpoint.getName());

        verify(monitoredEndpointRepository).save(any(MonitoredEndpoint.class));
    }

    @Test
    void testFindById() {
        when(monitoredEndpointRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.of(ENDPOINT));
        MonitoredEndpoint endpoint = monitoredEndpointService.findById(ID, OWNER);

        assertNotNull(endpoint);
        assertEquals(ID, endpoint.getId());
        assertEquals("Test Endpoint", endpoint.getName());
        assertEquals("http://example.com", endpoint.getUrl());
        assertEquals(60, endpoint.getMonitoredInterval());
        assertEquals(OWNER, endpoint.getOwner());

        verify(monitoredEndpointRepository).findById(any(UUID.class));
    }

    @Test
    void testFindAll() {
        when(monitoredEndpointRepository.findByOwner(OWNER)).thenReturn(List.of(ENDPOINT));
        List<MonitoredEndpoint> endpoints = monitoredEndpointService.findAll(OWNER);

        assertNotNull(endpoints);
        assertEquals(1, endpoints.size());
        assertEquals("Test Endpoint", endpoints.get(0).getName());
        assertEquals("http://example.com", endpoints.get(0).getUrl());
        assertEquals(60, endpoints.get(0).getMonitoredInterval());


        verify(monitoredEndpointRepository).findByOwner(OWNER);
    }

    @Test
    void testDelete() {
        when(monitoredEndpointRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.of(ENDPOINT));
        monitoredEndpointService.delete(ID, OWNER);

        assertFalse(monitoredEndpointRepository.existsById(ID));

        verify(monitoredEndpointRepository).findById(any(UUID.class));
        verify(monitoredEndpointRepository).delete(any(MonitoredEndpoint.class));
    }

    @Test
    void testUpdateMonitoredEndpoint() {

        when(monitoredEndpointRepository.findById(ID)).thenReturn(Optional.of(ENDPOINT));

        MonitoredEndpointRequest request = new MonitoredEndpointRequest("New Name", "http://newurl.com", 120);
        MonitoredEndpoint updatedEndpoint = MonitoredEndpoint.builder()
                .id(ID)
                .name(request.name())
                .url(request.url())
                .monitoredInterval(request.monitoredInterval())
                .owner(OWNER)
                .build();
        when(monitoredEndpointRepository.save(any(MonitoredEndpoint.class))).thenReturn(updatedEndpoint);

        MonitoredEndpoint result = monitoredEndpointService.updateMonitoredEndpoint(ID, request, OWNER);

        assertEquals(ID, result.getId());
        assertEquals(request.name(), result.getName());
        assertEquals(request.url(), result.getUrl());
        assertEquals(request.monitoredInterval(), result.getMonitoredInterval());
        assertEquals(OWNER, result.getOwner());

        verify(monitoredEndpointRepository).findById(ID);
        verify(monitoredEndpointRepository).save(any(MonitoredEndpoint.class));
    }

    @Test
    void testGetLast10MonitoringResults() {
        List<MonitoringResult> expectedResults = MonitoringResultTestingUtils.createMonitoringResults(10, ENDPOINT);
        when(monitoringResultRepository.findTop10ByMonitoredEndpointOrderByCheckDateDesc(ENDPOINT)).thenReturn(expectedResults);

        List<MonitoringResult> actualResults = monitoredEndpointService.getMonitoringResults(ENDPOINT);
        assertEquals(10, actualResults.size());
    }
}