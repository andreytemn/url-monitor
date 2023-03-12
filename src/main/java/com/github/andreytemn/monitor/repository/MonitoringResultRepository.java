package com.github.andreytemn.monitor.repository;

import com.github.andreytemn.monitor.model.MonitoringResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * CRUD repository for {@link MonitoringResult}
 */
@Repository
public interface MonitoringResultRepository extends JpaRepository<MonitoringResult, UUID> {}
