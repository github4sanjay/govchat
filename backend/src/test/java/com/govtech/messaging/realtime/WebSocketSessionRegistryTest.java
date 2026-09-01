package com.govtech.messaging.realtime;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class WebSocketSessionRegistryTest {
    private final WebSocketSessionRegistry registry = new WebSocketSessionRegistry();

    @Test
    void retainsOtherTabsWhenOneSessionCloses() {
        UUID userId = UUID.randomUUID();
        WebSocketSession first = mock(WebSocketSession.class);
        WebSocketSession second = mock(WebSocketSession.class);
        registry.add(userId, first);
        registry.add(userId, second);
        registry.remove(userId, first);
        assertTrue(registry.isOnline(userId));
        assertEquals(1, registry.sessionsFor(userId).size());
        assertTrue(registry.sessionsFor(userId).contains(second));
    }

    @Test
    void removesUserAfterLastSessionCloses() {
        UUID userId = UUID.randomUUID();
        WebSocketSession session = mock(WebSocketSession.class);
        registry.add(userId, session);
        registry.remove(userId, session);
        assertFalse(registry.isOnline(userId));
        assertTrue(registry.sessionsFor(userId).isEmpty());
    }
}
