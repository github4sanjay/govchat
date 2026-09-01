package com.govtech.messaging.auth;

import com.govtech.messaging.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class MessagingPrincipal implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final String username;
    private final String displayName;
    private final String password;
    private final boolean enabled;

    private MessagingPrincipal(UUID id, String username, String displayName, String password, boolean enabled) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.password = password;
        this.enabled = enabled;
    }

    public static MessagingPrincipal from(User user) {
        return new MessagingPrincipal(user.getId(), user.getUsername(), user.getDisplayName(),
                user.getPasswordHash(), user.isEnabled());
    }

    public UUID id() { return id; }
    public String displayName() { return displayName; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isEnabled() { return enabled; }
}
