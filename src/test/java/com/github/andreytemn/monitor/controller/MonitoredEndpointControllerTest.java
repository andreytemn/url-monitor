package com.github.andreytemn.monitor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoredEndpointRequest;
import com.github.andreytemn.monitor.model.User;
import com.github.andreytemn.monitor.service.MonitoredEndpointService;
import com.github.andreytemn.monitor.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for {@link MonitoredEndpointController}
 */
@ExtendWith(MockitoExtension.class)
class MonitoredEndpointControllerTest {

    public static final User USER =
            User.builder().username("user").email("user@example.com").accessToken("token").build();
    public static final MonitoredEndpoint ENDPOINT = MonitoredEndpoint.builder()
            .id(UUID.randomUUID())
            .name("Google")
            .url("https://www.google.com")
            .monitoredInterval(60)
            .owner(USER)
            .build();
    public static final UUID ID = ENDPOINT.getId();
    private MockMvc mockMvc;

    @Mock
    private MonitoredEndpointService monitoredEndpointService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MonitoredEndpointController monitoredEndpointController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(monitoredEndpointController).build();
    }

    @Test
    void testCreateMonitoredEndpoint() throws Exception {
        MonitoredEndpointRequest request = new MonitoredEndpointRequest("Google", "https://www.google.com", 60);

        when(userService.validateAccessToken("token")).thenReturn(USER);
        when(monitoredEndpointService.save(request, USER)).thenReturn(ENDPOINT);

        mockMvc.perform(post("/endpoints")
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .header("AccessToken", "token"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.url").value(request.url()))
                .andExpect(jsonPath("$.monitoredInterval").value(request.monitoredInterval()));

        verify(monitoredEndpointService).save(request, USER);
    }

    @Test
    void testUpdateMonitoredEndpoint() throws Exception {
        MonitoredEndpointRequest request = new MonitoredEndpointRequest("Google Updated", "https://www.google.com",
                120);

        MonitoredEndpoint updatedEndpoint = MonitoredEndpoint.builder()
                .id(UUID.randomUUID())
                .name("Google Updated")
                .url("https://www.google.com")
                .monitoredInterval(120)
                .owner(USER)
                .build();

        when(userService.validateAccessToken("token")).thenReturn(USER);
        when(monitoredEndpointService.updateMonitoredEndpoint(any(UUID.class), any(MonitoredEndpointRequest.class),
                eq(USER))).thenReturn(updatedEndpoint);

        mockMvc.perform(put("/endpoints/{id}", updatedEndpoint.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .header("AccessToken", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedEndpoint.getId().toString()))
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.url").value(request.url()))
                .andExpect(jsonPath("$.monitoredInterval").value(request.monitoredInterval()));

        verify(monitoredEndpointService).updateMonitoredEndpoint(updatedEndpoint.getId(), request, USER);
    }

    @Test
    void testDeleteMonitoredEndpoint() throws Exception {
        when(userService.validateAccessToken("token")).thenReturn(USER);
        mockMvc.perform(delete("/endpoints/{id}", ID).header("AccessToken", "token"))
                .andExpect(status().isNoContent());

        verify(monitoredEndpointService).delete(ID, USER);
    }

    @Test
    void testGetMonitoredEndpointById() throws Exception {
        when(monitoredEndpointService.findById(ID, USER)).thenReturn(ENDPOINT);
        when(userService.validateAccessToken("token")).thenReturn(USER);

        mockMvc.perform(get("/endpoints/{id}", ID).header("AccessToken", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.name").value(ENDPOINT.getName()))
                .andExpect(jsonPath("$.url").value(ENDPOINT.getUrl()))
                .andExpect(jsonPath("$.monitoredInterval").value(ENDPOINT.getMonitoredInterval()));

        verify(monitoredEndpointService).findById(ID, USER);
    }


    @Test
    void testGetAllMonitoredEndpoints() throws Exception {

        MonitoredEndpoint endpoint2 = MonitoredEndpoint.builder()
                .id(UUID.randomUUID())
                .name("Facebook")
                .url("https://www.facebook.com")
                .monitoredInterval(120)
                .owner(USER)
                .build();

        when(monitoredEndpointService.findAll(USER)).thenReturn(List.of(ENDPOINT, endpoint2));
        when(userService.validateAccessToken("token")).thenReturn(USER);

        mockMvc.perform(get("/endpoints").header("AccessToken", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(ID.toString()))
                .andExpect(jsonPath("$[0].name").value(ENDPOINT.getName()))
                .andExpect(jsonPath("$[0].url").value(ENDPOINT.getUrl()))
                .andExpect(jsonPath("$[0].monitoredInterval").value(ENDPOINT.getMonitoredInterval()))
                .andExpect(jsonPath("$[1].id").value(endpoint2.getId().toString()))
                .andExpect(jsonPath("$[1].name").value(endpoint2.getName()))
                .andExpect(jsonPath("$[1].url").value(endpoint2.getUrl()))
                .andExpect(jsonPath("$[1].monitoredInterval").value(endpoint2.getMonitoredInterval()));

        verify(monitoredEndpointService).findAll(USER);
    }

    @Test
    void testNotSignedRequestedAreInvalid() throws Exception {
        MonitoredEndpointRequest request = new MonitoredEndpointRequest("Google", "https://www.google.com", 120);
        mockMvc.perform(post("/endpoints")
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request))).andExpect(status().isBadRequest());
        mockMvc.perform(put("/endpoints/{id}", ENDPOINT.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request))).andExpect(status().isBadRequest());
        mockMvc.perform(get("/endpoints")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/endpoints/{id}", ID)).andExpect(status().isBadRequest());
        mockMvc.perform(delete("/endpoints/{id}", ID)).andExpect(status().isBadRequest());
    }

    @Test
    void testNotAuthorizedUserCannotDoPost() throws Exception {
        throwWhenInvalidToken();
        MonitoredEndpointRequest request = new MonitoredEndpointRequest("Google", "https://www.google.com", 120);
        mockMvc.perform(post("/endpoints")
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .header("AccessToken", "invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testNotAuthorizedUserCannotDoPut() throws Exception {
        throwWhenInvalidToken();
        MonitoredEndpointRequest request = new MonitoredEndpointRequest("Google", "https://www.google.com", 120);
        mockMvc.perform(put("/endpoints/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .header("AccessToken", "invalidToken"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void testNotAuthorizedUserCannotDoDelete() throws Exception {
        throwWhenInvalidToken();

        mockMvc.perform(delete("/endpoints/{id}", ID).header("AccessToken", "invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testNotAuthorizedUserCannotDoGet() throws Exception {
        throwWhenInvalidToken();

        mockMvc.perform(get("/endpoints/{id}", ID).header("AccessToken", "invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testNotAuthorizedUserCannotDoGetAll() throws Exception {
        throwWhenInvalidToken();

        mockMvc.perform(get("/endpoints").header("AccessToken", "invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    private void throwWhenInvalidToken() {
        when(userService.validateAccessToken("invalidToken")).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Invalid access token"));
    }
}