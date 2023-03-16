package com.github.andreytemn.monitor.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Represents the user of the service.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(name = "access_token", nullable = false)
    private String accessToken;
}