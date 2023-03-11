package com.github.andreytemn.monitor.model;

/**
 * A record representing a request body for {@link MonitoredEndpoint}
 *
 * @param name              name of the endpoint to monitor
 * @param url               the url to monitor
 * @param monitoredInterval the interval of monitoring in seconds
 */
public record MonitoredEndpointRequest(String name, String url, Integer monitoredInterval) {}