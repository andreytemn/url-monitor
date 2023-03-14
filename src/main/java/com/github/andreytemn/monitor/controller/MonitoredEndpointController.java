package com.github.andreytemn.monitor.controller;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoredEndpointRequest;
import com.github.andreytemn.monitor.service.MonitoredEndpointService;
import com.github.andreytemn.monitor.service.UserService;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/endpoints")
@Validated
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
    public MonitoredEndpoint createMonitoredEndpoint(@Valid @RequestBody MonitoredEndpointRequest monitoredEndpoint,
                                                     @RequestHeader("AccessToken") String accessToken) {
        return monitoredEndpointService.save(monitoredEndpoint, userService.validateAccessToken(accessToken));
    }

    @PutMapping("/{id}")
    public MonitoredEndpoint updateMonitoredEndpoint(@PathVariable UUID id,
                                                     @Valid @RequestBody MonitoredEndpointRequest monitoredEndpoint,
                                                     @RequestHeader("AccessToken") String accessToken) {
        return monitoredEndpointService.updateMonitoredEndpoint(id, monitoredEndpoint,
                userService.validateAccessToken(accessToken));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMonitoredEndpoint(@PathVariable UUID id, @RequestHeader("AccessToken") String accessToken) {
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