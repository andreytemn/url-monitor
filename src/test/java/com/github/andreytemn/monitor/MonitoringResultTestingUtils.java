package com.github.andreytemn.monitor;

import com.github.andreytemn.monitor.model.MonitoredEndpoint;
import com.github.andreytemn.monitor.model.MonitoringResult;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Simplifies preparing test data for MonitoringResult
 */
@UtilityClass
public class MonitoringResultTestingUtils {

    /**
     * Create a list of required number of {@link MonitoringResult} which belong to the given {@link MonitoredEndpoint}
     *
     * @param number   the required number of results
     * @param endpoint the endpoint which the results belong to
     * @return list of results
     */
    public List<MonitoringResult> createMonitoringResults(int number, MonitoredEndpoint endpoint) {
        List<MonitoringResult> results = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            results.add(MonitoringResult.builder().checkDate(LocalDateTime.now().minusMinutes(i)).httpStatusCode(200).payload("OK").monitoredEndpoint(endpoint).build());
        }
        return results;
    }
}
