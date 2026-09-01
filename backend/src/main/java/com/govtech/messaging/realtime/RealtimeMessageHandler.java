package com.govtech.messaging.realtime;

import com.govtech.messaging.auth.MessagingPrincipal;
import com.govtech.messaging.realtime.protocol.ClientCommand;
import com.govtech.messaging.realtime.protocol.ServerEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

@Component
public class RealtimeMessageHandler extends TextWebSocketHandler {
    private final WebSocketSessionRegistry sessions;
    private final RealtimePublisher publisher;
    private final RealtimeCommandService commands;
    private final ObjectMapper json;

    public RealtimeMessageHandler(WebSocketSessionRegistry sessions, RealtimePublisher publisher,
                                  RealtimeCommandService commands, ObjectMapper json) {
        this.sessions = sessions;
        this.publisher = publisher;
        this.commands = commands;
        this.json = json;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        sessions.add(userId(session), session);
        publisher.send(session, ServerEvent.connected());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage payload) throws IOException {
        try {
            commands.handle(userId(session), json.readValue(payload.getPayload(), ClientCommand.class));
        } catch (RuntimeException exception) {
            publisher.send(session, ServerEvent.error(exception.getMessage()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(userId(session), session);
    }

    private UUID userId(WebSocketSession session) {
        if (session.getPrincipal() instanceof Authentication authentication
                && authentication.getPrincipal() instanceof MessagingPrincipal principal) {
            return principal.id();
        }
        throw new IllegalStateException("Unauthenticated WebSocket session");
    }
}
