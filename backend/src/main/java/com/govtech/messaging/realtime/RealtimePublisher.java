package com.govtech.messaging.realtime;

import com.govtech.messaging.realtime.protocol.ServerEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

@Component
public class RealtimePublisher {
    private final WebSocketSessionRegistry sessions;
    private final ObjectMapper json;

    public RealtimePublisher(WebSocketSessionRegistry sessions, ObjectMapper json) {
        this.sessions = sessions;
        this.json = json;
    }

    public void sendToUsers(Collection<UUID> userIds, ServerEvent event) {
        userIds.forEach(userId -> sendToUser(userId, event));
    }

    public void sendToUser(UUID userId, ServerEvent event) {
        sessions.sessionsFor(userId).forEach(session -> sendSafely(session, event));
    }

    public void send(WebSocketSession session, ServerEvent event) throws IOException {
        synchronized (session) {
            session.sendMessage(new TextMessage(json.writeValueAsString(event)));
        }
    }

    private void sendSafely(WebSocketSession session, ServerEvent event) {
        if (!session.isOpen()) return;
        try {
            send(session, event);
        } catch (IOException exception) {
            try { session.close(CloseStatus.SERVER_ERROR); } catch (IOException ignored) {}
        }
    }
}
