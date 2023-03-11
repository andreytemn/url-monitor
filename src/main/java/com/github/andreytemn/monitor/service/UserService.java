package com.github.andreytemn.monitor.service;

import com.github.andreytemn.monitor.model.User;
import com.github.andreytemn.monitor.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * A service that allows read-only access for {@link User}
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Find a user by access token. Throws an UNAUTHORIZED exception if none found.
     *
     * @param accessToken the accessToken assigned to a user
     * @return the user if found
     * @throws ResponseStatusException if the user not found
     */
    public User validateAccessToken(String accessToken) {
        User user = userRepository.findByAccessToken(accessToken);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token");
        }
        return user;
    }
}