package com.github.andreytemn.monitor.repository;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * A CRUD repository for {@link MonitoredEndpoint} that allows also find all the entities that belong to the user
 */
public interface MonitoredEndpointRepository extends JpaRepository<MonitoredEndpoint, UUID> {
    /**
     * Find all the monitored endpoints that belong to the user
     *
     * @param owner the owner of the {@link MonitoredEndpoint}
     * @return a list of all {@link MonitoredEndpoint} or empty.
     */
    @NonNull List<MonitoredEndpoint> findByOwner(User owner);
}