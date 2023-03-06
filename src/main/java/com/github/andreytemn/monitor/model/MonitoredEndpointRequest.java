package com.github.andreytemn.monitor.model;

public record MonitoredEndpointRequest(String name, String url, Integer monitoredInterval){}