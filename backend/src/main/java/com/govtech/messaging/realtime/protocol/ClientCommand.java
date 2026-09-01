package com.govtech.messaging.realtime.protocol;

import java.util.UUID;

public record ClientCommand(Type type, UUID clientMessageId, UUID recipientId, String content) {
    public enum Type { SEND_MESSAGE }
}
