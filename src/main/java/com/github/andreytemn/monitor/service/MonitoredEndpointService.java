package com.github.andreytemn.monitor.service;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoredEndpointRequest;
import com.github.andreytemn.monitor.model.MonitoringResult;
import com.github.andreytemn.monitor.model.User;
import com.github.andreytemn.monitor.repository.MonitoredEndpointRepository;
import com.github.andreytemn.monitor.repository.MonitoringResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service class for managing {@link MonitoredEndpoint} entities.
 */
@Service
public class MonitoredEndpointService {

    @Autowired
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @Autowired
    private MonitoringResultRepository monitoringResultRepository;

    /**
     * Save a new {@link MonitoredEndpoint} entity with the specified parameters.
     *
     * @param monitoredEndpoint the {@link MonitoredEndpointRequest} object containing the name, URL, and monitored
     *                          interval of the endpoint
     * @param owner             the {@link User} object representing the owner of the endpoint
     * @return the saved {@link MonitoredEndpoint} entity
     */
    public MonitoredEndpoint save(MonitoredEndpointRequest monitoredEndpoint, User owner) {
        return monitoredEndpointRepository.save(MonitoredEndpoint.builder()
                .name(monitoredEndpoint.name())
                .url(monitoredEndpoint.url())
                .monitoredInterval(monitoredEndpoint.monitoredInterval())
                .lastCheckAt(LocalDateTime.now())
                .owner(owner).build());
    }

    /**
     * Find the {@link MonitoredEndpoint} entity with the specified ID and owner.
     *
     * @param id    the UUID of the {@link MonitoredEndpoint} entity
     * @param owner the {@link User} object representing the owner of the endpoint
     * @return the found {@link MonitoredEndpoint} entity
     * @throws ResponseStatusException if the endpoint is not found or does not belong to the specified owner
     */
    public MonitoredEndpoint findById(UUID id, User owner) {
        MonitoredEndpoint endpoint =
                monitoredEndpointRepository.findById(id).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MonitoredEndpoint not found"));
        if (!endpoint.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "MonitoredEndpoint does not belong to this user");
        }
        return endpoint;
    }

    /**
     * Find all {@link MonitoredEndpoint} entities owned by the specified user.
     *
     * @param owner the {@link User} object representing the owner of the endpoints
     * @return a list of {@link MonitoredEndpoint} entities owned by the specified user
     */
    public List<MonitoredEndpoint> findAll(User owner) {
        return monitoredEndpointRepository.findByOwner(owner);
    }

    /**
     * Delete the {@link MonitoredEndpoint} entity with the specified ID and owner.
     *
     * @param id    the UUID of the {@link MonitoredEndpoint} entity
     * @param owner the {@link User} object representing the owner of the endpoint
     * @throws ResponseStatusException if the endpoint is not found or does not belong to the specified owner
     */
    public void delete(UUID id, User owner) {
        monitoredEndpointRepository.delete(findById(id, owner));
    }

    /**
     * Update the {@link MonitoredEndpoint} entity with the specified ID and owner.
     *
     * @param id                the UUID of the {@link MonitoredEndpoint} entity to update
     * @param monitoredEndpoint the {@link MonitoredEndpointRequest} object containing the updated name, URL, and
     *                          monitored interval of the endpoint
     * @param owner             the {@link User} object representing the owner of the endpoint
     * @return the updated {@link MonitoredEndpoint} entity
     * @throws ResponseStatusException if the endpoint is not found or does not belong to the specified owner
     */
    public MonitoredEndpoint updateMonitoredEndpoint(UUID id, MonitoredEndpointRequest monitoredEndpoint, User owner) {
        MonitoredEndpoint existingEndpoint = findById(id, owner).toBuilder()
                .name(monitoredEndpoint.name())
                .url(monitoredEndpoint.url())
                .monitoredInterval(monitoredEndpoint.monitoredInterval())
                .build();

        return monitoredEndpointRepository.save(existingEndpoint);
    }

    /**
     * Get the last 10 {@link MonitoringResult} entities for the specified {@link MonitoredEndpoint}.
     *
     * @param endpoint the {@link MonitoredEndpoint} entity to retrieve monitoring results for
     * @return a list of the last 10 {@link MonitoringResult} entities for the specified {@link MonitoredEndpoint}
     */
    public List<MonitoringResult> getMonitoringResults(MonitoredEndpoint endpoint) {
        return monitoringResultRepository.findTop10ByMonitoredEndpointOrderByCheckDateDesc(endpoint);
    }
}