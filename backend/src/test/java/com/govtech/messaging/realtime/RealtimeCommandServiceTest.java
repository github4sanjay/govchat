package com.govtech.messaging.realtime;

import com.govtech.messaging.message.MessageResponse;
import com.govtech.messaging.message.MessageService;
import com.govtech.messaging.realtime.protocol.ClientCommand;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RealtimeCommandServiceTest {
    @Test
    void sendsPersistedMessageToBothParticipants() {
        MessageService messages = mock(MessageService.class);
        RealtimePublisher publisher = mock(RealtimePublisher.class);
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        MessageResponse saved = new MessageResponse(UUID.randomUUID(), clientId,
                senderId, recipientId, "hello", Instant.now());
        when(messages.send(senderId, clientId, recipientId, "hello")).thenReturn(saved);
        RealtimeCommandService service = new RealtimeCommandService(messages, publisher);

        service.handle(senderId, new ClientCommand(ClientCommand.Type.SEND_MESSAGE, clientId, recipientId, "hello"));

        verify(messages).send(senderId, clientId, recipientId, "hello");
        verify(publisher).sendToUsers(eq(Set.of(senderId, recipientId)), any());
    }
}
