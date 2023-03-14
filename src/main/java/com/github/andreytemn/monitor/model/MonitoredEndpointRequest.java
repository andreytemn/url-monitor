package com.github.andreytemn.monitor.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

/**
 * A record representing a request body for {@link MonitoredEndpoint}
 *
 * @param name              name of the endpoint to monitor
 * @param url               the url to monitor
 * @param monitoredInterval the interval of monitoring in seconds
 */
public record MonitoredEndpointRequest(
        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotBlank(message = "URL cannot be blank")
        @URL(message = "Invalid URL format")
        String url,

        @NotNull(message = "Monitored interval cannot be null")
        @Positive(message = "Monitored interval must be a positive number")
        Integer monitoredInterval
) {}