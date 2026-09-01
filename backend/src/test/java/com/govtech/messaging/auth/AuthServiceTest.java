package com.govtech.messaging.auth;

import com.govtech.messaging.user.User;
import com.govtech.messaging.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    private final UserRepository users = mock(UserRepository.class);
    private final BCryptPasswordEncoder passwords = new BCryptPasswordEncoder(4);
    private final AuthService service = new AuthService(users, passwords);

    @Test
    void registrationStoresAHashInsteadOfTheRawPassword() {
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MessagingPrincipal principal = service.register("New_User", "New User", "secret123");

        assertEquals("new_user", principal.getUsername());
        assertNotEquals("secret123", principal.getPassword());
        assertTrue(passwords.matches("secret123", principal.getPassword()));
    }

    @Test
    void duplicateUsernameIsRejectedBeforeHashing() {
        when(users.existsByUsernameIgnoreCase("alice")).thenReturn(true);

        assertThrows(AuthService.UsernameAlreadyExistsException.class,
                () -> service.register("Alice", "Another Alice", "secret123"));
        verify(users, never()).save(any());
    }
}
