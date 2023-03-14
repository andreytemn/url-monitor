package com.github.andreytemn.monitor.repository;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoringResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * CRUD repository for {@link MonitoringResult}
 */
@Repository
public interface MonitoringResultRepository extends JpaRepository<MonitoringResult, UUID> {
    List<MonitoringResult> findTop10ByMonitoredEndpointOrderByCheckDateDesc(MonitoredEndpoint endpoint);
}
