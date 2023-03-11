package com.github.andreytemn.monitor.service;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoredEndpointRequest;
import com.github.andreytemn.monitor.model.User;
import com.github.andreytemn.monitor.repository.MonitoredEndpointRepository;
import org.junit.jupiter.api.BeforeEach;
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

    public static final User OWNER =
            User.builder().email("user@email.com").username("user").accessToken("token").build();

    @Mock
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @InjectMocks
    private MonitoredEndpointService monitoredEndpointService;


    private final MonitoredEndpoint testEndpoint = MonitoredEndpoint.builder()
            .id(UUID.randomUUID())
            .name("Test Endpoint")
            .url("http://example.com")
            .monitoredInterval(60)
            .lastCheckAt(LocalDateTime.now())
            .owner(OWNER)
            .build();

    @BeforeEach
    public void setup() {
//        List<MonitoredEndpoint> endpoints = new ArrayList<>();
//        MonitoredEndpoint testEndpoint = MonitoredEndpoint.builder()
//                .id(UUID.randomUUID())
//                .name("Test Endpoint")
//                .url("http://example.com")
//                .monitoredInterval(60)
//                .lastCheckAt(LocalDateTime.now())
//                .owner(owner)
//                .build();
//
//        testEndpointId = testEndpoint.getId();
//
//        endpoints.add(testEndpoint);
//
//        when(monitoredEndpointRepository.findByOwner(any(User.class))).thenReturn(endpoints);
//        when(monitoredEndpointRepository.save(any(MonitoredEndpoint.class))).thenReturn(testEndpoint);
//        when(monitoredEndpointRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.of(testEndpoint));
    }

    @Test
    void testSaveEndpoint() {
        MonitoredEndpointRequest request = new MonitoredEndpointRequest("Test Endpoint", "http://example.com", 60);

        when(monitoredEndpointRepository.save(any(MonitoredEndpoint.class))).thenReturn(testEndpoint);
        MonitoredEndpoint savedEndpoint = monitoredEndpointService.save(request, OWNER);

        assertNotNull(savedEndpoint.getId());
        assertEquals(request.name(), savedEndpoint.getName());

        verify(monitoredEndpointRepository).save(any(MonitoredEndpoint.class));
    }

    @Test
    void testFindById() {
        when(monitoredEndpointRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.of(testEndpoint));
        MonitoredEndpoint endpoint = monitoredEndpointService.findById(testEndpoint.getId(), OWNER);

        assertNotNull(endpoint);
        assertEquals(testEndpoint.getId(), endpoint.getId());
        assertEquals("Test Endpoint", endpoint.getName());
        assertEquals("http://example.com", endpoint.getUrl());
        assertEquals(60, endpoint.getMonitoredInterval());
        assertEquals(OWNER, endpoint.getOwner());

        verify(monitoredEndpointRepository).findById(any(UUID.class));
    }

    @Test
    void testFindAll() {
        when(monitoredEndpointRepository.findByOwner(OWNER)).thenReturn(List.of(testEndpoint));
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
        when(monitoredEndpointRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.of(testEndpoint));
        monitoredEndpointService.delete(testEndpoint.getId(), OWNER);

        assertFalse(monitoredEndpointRepository.existsById(testEndpoint.getId()));

        verify(monitoredEndpointRepository).findById(any(UUID.class));
        verify(monitoredEndpointRepository).delete(any(MonitoredEndpoint.class));
    }

    @Test
    void testUpdateMonitoredEndpoint() {
        UUID endpointId = UUID.randomUUID();

        MonitoredEndpoint existingEndpoint = MonitoredEndpoint.builder()
                .id(endpointId)
                .name("Old Name")
                .url("http://oldurl.com")
                .monitoredInterval(60)
                .owner(OWNER)
                .build();
        when(monitoredEndpointRepository.findById(endpointId)).thenReturn(Optional.of(existingEndpoint));

        MonitoredEndpointRequest request = new MonitoredEndpointRequest("New Name", "http://newurl.com", 120);
        MonitoredEndpoint updatedEndpoint = MonitoredEndpoint.builder()
                .id(endpointId)
                .name(request.name())
                .url(request.url())
                .monitoredInterval(request.monitoredInterval())
                .owner(OWNER)
                .build();
        when(monitoredEndpointRepository.save(any(MonitoredEndpoint.class))).thenReturn(updatedEndpoint);

        MonitoredEndpoint result = monitoredEndpointService.updateMonitoredEndpoint(endpointId, request, OWNER);

        assertEquals(existingEndpoint.getId(), result.getId());
        assertEquals(request.name(), result.getName());
        assertEquals(request.url(), result.getUrl());
        assertEquals(request.monitoredInterval(), result.getMonitoredInterval());
        assertEquals(OWNER, result.getOwner());

        verify(monitoredEndpointRepository).findById(endpointId);
        verify(monitoredEndpointRepository).save(any(MonitoredEndpoint.class));
    }
}