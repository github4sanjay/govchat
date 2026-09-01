package com.govtech.messaging.auth;

import com.govtech.messaging.user.User;
import com.govtech.messaging.user.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwords;

    public AuthService(UserRepository users, PasswordEncoder passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    @Transactional
    public MessagingPrincipal register(String rawUsername, String rawDisplayName, String rawPassword) {
        String username = rawUsername == null ? "" : rawUsername.trim().toLowerCase();
        String displayName = rawDisplayName == null ? "" : rawDisplayName.trim();
        String password = rawPassword == null ? "" : rawPassword;

        if (!username.matches("[a-z0-9._-]{3,50}")) {
            throw new IllegalArgumentException("Username must be 3-50 lowercase letters, numbers, dots, dashes or underscores");
        }
        if (displayName.length() < 2 || displayName.length() > 100) {
            throw new IllegalArgumentException("Display name must contain between 2 and 100 characters");
        }
        if (password.length() < 8 || password.length() > 72) {
            throw new IllegalArgumentException("Password must contain between 8 and 72 characters");
        }
        if (users.existsByUsernameIgnoreCase(username)) {
            throw new UsernameAlreadyExistsException();
        }

        try {
            User saved = users.save(User.register(username, displayName, passwords.encode(password)));
            return MessagingPrincipal.from(saved);
        } catch (DataIntegrityViolationException race) {
            // The database unique constraint is the final arbiter for concurrent registrations.
            throw new UsernameAlreadyExistsException();
        }
    }

    public static class UsernameAlreadyExistsException extends RuntimeException {
        public UsernameAlreadyExistsException() {
            super("Username is already registered");
        }
    }
}
