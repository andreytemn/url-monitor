package com.github.andreytemn.monitor.repository;

import com.github.andreytemn.monitor.model.User;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * A repository for {@link User} that resolves the user by the accessToken.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Find a user by the access token
     *
     * @param accessToken the token of the user
     * @return a User or null
     */
    @Nullable
    User findByAccessToken(String accessToken);
}