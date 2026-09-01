package com.govtech.messaging.realtime;

import com.govtech.messaging.message.MessageResponse;
import com.govtech.messaging.message.MessageService;
import com.govtech.messaging.realtime.protocol.ClientCommand;
import com.govtech.messaging.realtime.protocol.ServerEvent;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class RealtimeCommandService {
    private final MessageService messages;
    private final RealtimePublisher publisher;

    public RealtimeCommandService(MessageService messages, RealtimePublisher publisher) {
        this.messages = messages;
        this.publisher = publisher;
    }

    public void handle(UUID senderId, ClientCommand command) {
        switch (command.type()) {
            case SEND_MESSAGE -> sendMessage(senderId, command);
        }
    }

    private void sendMessage(UUID senderId, ClientCommand command) {
        MessageResponse saved = messages.send(senderId, command.clientMessageId(),
                command.recipientId(), command.content());
        // Persist before publish so history can recover a missed live event.
        publisher.sendToUsers(Set.of(saved.senderId(), saved.recipientId()), ServerEvent.message(saved));
    }
}
