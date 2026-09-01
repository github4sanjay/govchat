package com.govtech.messaging.message;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID clientMessageId,
        UUID senderId,
        UUID recipientId,
        String content,
        Instant sentAt
) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(message.getId(), message.getClientMessageId(),
                message.getSender().getId(), message.getRecipient().getId(),
                message.getContent(), message.getSentAt());
    }
}
