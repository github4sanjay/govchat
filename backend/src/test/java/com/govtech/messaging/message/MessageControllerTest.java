package com.govtech.messaging.message;

import com.govtech.messaging.auth.MessagingPrincipal;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class MessageControllerTest {
    @Test
    void historyUsesAuthenticatedIdentity() {
        MessageService messages = mock(MessageService.class);
        MessagingPrincipal principal = mock(MessagingPrincipal.class);
        UUID authenticatedUserId = UUID.randomUUID();
        UUID peerId = UUID.randomUUID();
        List<MessageResponse> expected = List.of();
        when(principal.id()).thenReturn(authenticatedUserId);
        when(messages.conversation(authenticatedUserId, peerId)).thenReturn(expected);

        List<MessageResponse> actual = new MessageController(messages).conversation(principal, peerId);

        assertSame(expected, actual);
        verify(messages).conversation(authenticatedUserId, peerId);
    }
}
