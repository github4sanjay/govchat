package com.govtech.messaging.realtime;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionRegistry {
    private final ConcurrentHashMap<UUID, Set<WebSocketSession>> sessionsByUser = new ConcurrentHashMap<>();

    public void add(UUID userId, WebSocketSession session) {
        sessionsByUser.computeIfAbsent(userId, ignored -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void remove(UUID userId, WebSocketSession session) {
        sessionsByUser.computeIfPresent(userId, (ignored, sessions) -> {
            sessions.remove(session);
            return sessions.isEmpty() ? null : sessions;
        });
    }

    public Set<WebSocketSession> sessionsFor(UUID userId) {
        return Set.copyOf(sessionsByUser.getOrDefault(userId, Set.of()));
    }

    public boolean isOnline(UUID userId) {
        return sessionsByUser.containsKey(userId);
    }
}
