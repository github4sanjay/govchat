package com.govtech.messaging.message;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageServiceTest {
    private final MessageService service = new MessageService(null, null, Clock.systemUTC());

    @Test
    void rejectsBlankMessagesBeforePersistence() {
        assertThrows(IllegalArgumentException.class,
                () -> service.send(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "   "));
    }

    @Test
    void rejectsSendingToSelf() {
        UUID userId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> service.send(userId, UUID.randomUUID(), userId, "hello"));
    }
}
