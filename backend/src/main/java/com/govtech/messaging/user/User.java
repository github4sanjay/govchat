package com.govtech.messaging.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_user")
public class User {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled;

    protected User() {}

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isEnabled() { return enabled; }

    public static User register(String username, String displayName, String passwordHash) {
        User user = new User();
        user.id = UUID.randomUUID();
        user.username = username;
        user.displayName = displayName;
        user.passwordHash = passwordHash;
        user.enabled = true;
        user.createdAt = Instant.now();
        return user;
    }
}
