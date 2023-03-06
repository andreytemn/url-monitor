package com.github.andreytemn.monitor.repository;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MonitoredEndpointRepository extends JpaRepository<MonitoredEndpoint, UUID> {

}