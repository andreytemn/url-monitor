package com.github.andreytemn.monitor.controller;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoredEndpointRequest;
import com.github.andreytemn.monitor.model.MonitoringResult;
import com.github.andreytemn.monitor.service.MonitoredEndpointService;
import com.github.andreytemn.monitor.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Maps all the endpoint requests. The execution is delegated to {@link MonitoredEndpointService}.
 */
@RestController
@RequestMapping("/endpoints")
@Validated
@Slf4j
public class MonitoredEndpointController {
    @Autowired
    private MonitoredEndpointService monitoredEndpointService;

    @Autowired
    private UserService userService;

    /**
     * Return all {@link MonitoredEndpoint} for the user with given access token.
     *
     * @param accessToken the token of the registered user
     * @return a list of {@link MonitoredEndpoint} for the user or empty if none found
     */
    @GetMapping
    public List<MonitoredEndpoint> getAllMonitoredEndpoints(@RequestHeader("AccessToken") String accessToken) {
        log.debug("Getting all monitored endpoints for the user");
        return monitoredEndpointService.findAll(userService.validateAccessToken(accessToken));
    }

    /**
     * Return 10 last {@link MonitoringResult} of the endpoint with the id, for the user with given access token.
     *
     * @param id          the id of the {@link MonitoredEndpoint}
     * @param accessToken the token of the registered user
     * @return a list of not more than 10 {@link MonitoringResult} for the user or empty if none found
     */
    @GetMapping("/{id}/results")
    public List<MonitoringResult> getMonitoringResults(@PathVariable UUID id,
                                                       @RequestHeader("AccessToken") String accessToken) {
        log.debug("Getting monitoring results for the endpoint with id {}", id);
        return monitoredEndpointService.getMonitoringResults(monitoredEndpointService.findById(id,
                userService.validateAccessToken(accessToken)));
    }

    /**
     * Get the {@link MonitoredEndpoint} with the id, for the user with given access token.
     *
     * @param id          the id of the {@link MonitoredEndpoint}
     * @param accessToken the token of the registered user
     * @return the {@link MonitoredEndpoint}
     */
    @GetMapping("/{id}")
    public MonitoredEndpoint getMonitoredEndpointById(@PathVariable UUID id,
                                                      @RequestHeader("AccessToken") String accessToken) {
        log.debug("Getting monitored endpoint with id {}", id);
        return monitoredEndpointService.findById(id, userService.validateAccessToken(accessToken));
    }


    /**
     * Create a {@link MonitoredEndpoint}
     *
     * @param monitoredEndpoint the  {@link MonitoredEndpointRequest}
     * @param accessToken       the token of the registered user
     * @return the created {@link MonitoredEndpoint}
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MonitoredEndpoint createMonitoredEndpoint(@Valid @RequestBody MonitoredEndpointRequest monitoredEndpoint,
                                                     @RequestHeader("AccessToken") String accessToken) {
        log.debug("Creating a monitored endpoint {}", monitoredEndpoint.toString());
        return monitoredEndpointService.save(monitoredEndpoint, userService.validateAccessToken(accessToken));
    }

    /**
     * Update the {@link MonitoredEndpoint} with the id
     *
     * @param id                the id of the {@link MonitoredEndpoint} to update
     * @param monitoredEndpoint the  {@link MonitoredEndpointRequest}
     * @param accessToken       the token of the registered user
     * @return the created {@link MonitoredEndpoint}
     */
    @PutMapping("/{id}")
    public MonitoredEndpoint updateMonitoredEndpoint(@PathVariable UUID id,
                                                     @Valid @RequestBody MonitoredEndpointRequest monitoredEndpoint,
                                                     @RequestHeader("AccessToken") String accessToken) {
        log.debug("Update the monitored endpoint with id {} {}", id, monitoredEndpoint.toString());
        return monitoredEndpointService.updateMonitoredEndpoint(id, monitoredEndpoint,
                userService.validateAccessToken(accessToken));
    }

    /**
     * Delete the {@link MonitoredEndpoint}
     *
     * @param id          the id of the {@link MonitoredEndpoint} to delete
     * @param accessToken the token of the registered user
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMonitoredEndpoint(@PathVariable UUID id, @RequestHeader("AccessToken") String accessToken) {
        log.debug("Deleting a monitored endpoint with id{}", id);
        monitoredEndpointService.delete(id, userService.validateAccessToken(accessToken));
    }

    /**
     * Create a descriptive error response for {@link MethodArgumentNotValidException}
     *
     * @param ex the caught exception
     * @return the response entity for this error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST,
                createErrorMessage(ex.getBindingResult())).build());
    }

    private static String createErrorMessage(BindingResult result) {
        return result.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining());
    }
}