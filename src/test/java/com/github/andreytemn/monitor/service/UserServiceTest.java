package com.github.andreytemn.monitor.service;

import com.github.andreytemn.monitor.model.User;
import com.github.andreytemn.monitor.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link UserService}
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testValidateAccessTokenWithValidToken() {
        String accessToken = "validAccessToken";
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .email("test@example.com")
                .accessToken(accessToken).build();
        when(userRepository.findByAccessToken(accessToken)).thenReturn(user);

        User result = userService.validateAccessToken(accessToken);
        assertEquals(result, user);
        verify(userRepository).findByAccessToken(accessToken);
    }

    @Test
    void testValidateAccessTokenWithInvalidToken() {
        String accessToken = "invalidAccessToken";
        when(userRepository.findByAccessToken(accessToken)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.validateAccessToken(accessToken));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid access token", exception.getReason());
        verify(userRepository).findByAccessToken(accessToken);
    }
}