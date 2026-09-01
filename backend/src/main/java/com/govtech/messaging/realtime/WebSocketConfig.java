package com.govtech.messaging.realtime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final RealtimeMessageHandler handler;
    private final String allowedOrigin;

    public WebSocketConfig(RealtimeMessageHandler handler,
                           @Value("${app.cors-allowed-origin}") String allowedOrigin) {
        this.handler = handler;
        this.allowedOrigin = allowedOrigin;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws")
                .setAllowedOrigins(allowedOrigin);
    }
}
