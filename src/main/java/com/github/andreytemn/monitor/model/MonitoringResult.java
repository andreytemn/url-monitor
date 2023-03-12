package com.github.andreytemn.monitor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the result of monitoring of the endpoint.
 */
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "monitoring_result")
public class MonitoringResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "check_date", nullable = false)
    private LocalDateTime checkDate;

    @Column(name = "status_code", nullable = false)
    private Integer httpStatusCode;

    @Column(name = "payload", nullable = false)
    private String payload;

    @ManyToOne
    @JoinColumn(name = "monitored_endpoint_id", nullable = false)
    private MonitoredEndpoint monitoredEndpoint;

}