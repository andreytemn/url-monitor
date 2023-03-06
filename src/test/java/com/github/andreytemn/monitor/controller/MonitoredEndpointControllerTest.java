package com.github.andreytemn.monitor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoredEndpointRequest;
import com.github.andreytemn.monitor.service.MonitoredEndpointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MonitoredEndpointControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MonitoredEndpointService monitoredEndpointService;

    @InjectMocks
    private MonitoredEndpointController monitoredEndpointController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(monitoredEndpointController).build();
    }

    @Test
    void testCreateMonitoredEndpoint() throws Exception {
        MonitoredEndpointRequest request = new MonitoredEndpointRequest("Google", "https://www.google.com", 60);

        MonitoredEndpoint monitoredEndpoint = MonitoredEndpoint.builder()
                .id(UUID.randomUUID())
                .name(request.name())
                .url(request.url())
                .monitoredInterval(request.monitoredInterval())
                .build();

        when(monitoredEndpointService.save(any(MonitoredEndpointRequest.class))).thenReturn(monitoredEndpoint);

        mockMvc.perform(post("/endpoints")
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.url").value(request.url()))
                .andExpect(jsonPath("$.monitoredInterval").value(request.monitoredInterval()));

        verify(monitoredEndpointService).save(request);
    }

    @Test
    void testUpdateMonitoredEndpoint() throws Exception {
        MonitoredEndpointRequest request = new MonitoredEndpointRequest("Google Updated", "https://www.google.com", 120);

        MonitoredEndpoint monitoredEndpoint = MonitoredEndpoint.builder()
                .id(UUID.randomUUID())
                .name("Google Updated")
                .url("https://www.google.com")
                .monitoredInterval(120)
                .build();

        when(monitoredEndpointService.updateMonitoredEndpoint(any(UUID.class), any(MonitoredEndpointRequest.class)))
                .thenReturn(monitoredEndpoint);

        mockMvc.perform(put("/endpoints/{id}", monitoredEndpoint.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(monitoredEndpoint.getId().toString()))
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.url").value(request.url()))
                .andExpect(jsonPath("$.monitoredInterval").value(request.monitoredInterval()));

        verify(monitoredEndpointService).updateMonitoredEndpoint(monitoredEndpoint.getId(), request);
    }

    @Test
    void testDeleteMonitoredEndpoint() throws Exception {
        MonitoredEndpoint monitoredEndpoint = MonitoredEndpoint.builder()
                .id(UUID.randomUUID())
                .name("Google")
                .url("https://www.google.com")
                .monitoredInterval(60)
                .build();

        mockMvc.perform(delete("/endpoints/{id}", monitoredEndpoint.getId()))
                .andExpect(status().isNoContent());

        verify(monitoredEndpointService).delete(monitoredEndpoint.getId());
    }

    @Test
    void testGetMonitoredEndpointById() throws Exception {
        UUID id = UUID.randomUUID();

        MonitoredEndpoint monitoredEndpoint = MonitoredEndpoint.builder()
                .id(id)
                .name("Google")
                .url("https://www.google.com")
                .monitoredInterval(60)
                .build();

        when(monitoredEndpointService.findById(id))
                .thenReturn(monitoredEndpoint);

        mockMvc.perform(get("/endpoints/{id}", monitoredEndpoint.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(monitoredEndpoint.getId().toString()))
                .andExpect(jsonPath("$.name").value(monitoredEndpoint.getName()))
                .andExpect(jsonPath("$.url").value(monitoredEndpoint.getUrl()))
                .andExpect(jsonPath("$.monitoredInterval").value(monitoredEndpoint.getMonitoredInterval()));

        verify(monitoredEndpointService).findById(monitoredEndpoint.getId());
    }


    @Test
    void testGetAllMonitoredEndpoints() throws Exception {
        MonitoredEndpoint monitoredEndpoint1 = MonitoredEndpoint.builder()
                .id(UUID.randomUUID())
                .name("Google")
                .url("https://www.google.com")
                .monitoredInterval(60)
                .build();

        MonitoredEndpoint monitoredEndpoint2 = MonitoredEndpoint.builder()
                .id(UUID.randomUUID())
                .name("Facebook")
                .url("https://www.facebook.com")
                .monitoredInterval(120)
                .build();

        when(monitoredEndpointService.findAll()).thenReturn(List.of(monitoredEndpoint1, monitoredEndpoint2));

        mockMvc.perform(get("/endpoints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(monitoredEndpoint1.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(monitoredEndpoint1.getName()))
                .andExpect(jsonPath("$[0].url").value(monitoredEndpoint1.getUrl()))
                .andExpect(jsonPath("$[0].monitoredInterval").value(monitoredEndpoint1.getMonitoredInterval()))
                .andExpect(jsonPath("$[1].id").value(monitoredEndpoint2.getId().toString()))
                .andExpect(jsonPath("$[1].name").value(monitoredEndpoint2.getName()))
                .andExpect(jsonPath("$[1].url").value(monitoredEndpoint2.getUrl()))
                .andExpect(jsonPath("$[1].monitoredInterval").value(monitoredEndpoint2.getMonitoredInterval()));

        verify(monitoredEndpointService).findAll();
    }
}