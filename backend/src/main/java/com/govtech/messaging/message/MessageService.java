package com.govtech.messaging.message;

import com.govtech.messaging.user.User;
import com.govtech.messaging.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
    private final MessageRepository messages;
    private final UserRepository users;
    private final Clock clock;

    @Autowired
    public MessageService(MessageRepository messages, UserRepository users) {
        this(messages, users, Clock.systemUTC());
    }

    MessageService(MessageRepository messages, UserRepository users, Clock clock) {
        this.messages = messages;
        this.users = users;
        this.clock = clock;
    }

    @Transactional
    public MessageResponse send(UUID senderId, UUID clientMessageId, UUID recipientId, String rawContent) {
        String content = rawContent == null ? "" : rawContent.trim();
        if (content.isEmpty() || content.length() > 2000) {
            throw new IllegalArgumentException("Message must contain between 1 and 2000 characters");
        }
        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("Sender and recipient must be different users");
        }

        // A client-generated id makes retries after a dropped acknowledgement idempotent.
        return messages.findBySenderIdAndClientMessageId(senderId, clientMessageId)
                .map(MessageResponse::from)
                .orElseGet(() -> persist(senderId, clientMessageId, recipientId, content));
    }

    private MessageResponse persist(UUID senderId, UUID clientMessageId, UUID recipientId, String content) {
        User sender = users.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Unknown sender"));
        User recipient = users.findById(recipientId).orElseThrow(() -> new IllegalArgumentException("Unknown recipient"));
        Message message = new Message(UUID.randomUUID(), clientMessageId, sender, recipient, content, Instant.now(clock));
        return MessageResponse.from(messages.save(message));
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> conversation(UUID first, UUID second) {
        if (first.equals(second)) throw new IllegalArgumentException("Users must be different");
        return messages.findConversation(first, second).stream().map(MessageResponse::from).toList();
    }
}
