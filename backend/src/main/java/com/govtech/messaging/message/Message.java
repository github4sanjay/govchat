package com.govtech.messaging.message;

import com.govtech.messaging.user.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "message", uniqueConstraints = @UniqueConstraint(
        name = "uq_message_sender_client", columnNames = {"sender_id", "client_message_id"}))
public class Message {
    @Id
    private UUID id;

    @Column(name = "client_message_id", nullable = false)
    private UUID clientMessageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    protected Message() {}

    Message(UUID id, UUID clientMessageId, User sender, User recipient, String content, Instant sentAt) {
        this.id = id;
        this.clientMessageId = clientMessageId;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.sentAt = sentAt;
    }

    public UUID getId() { return id; }
    public UUID getClientMessageId() { return clientMessageId; }
    public User getSender() { return sender; }
    public User getRecipient() { return recipient; }
    public String getContent() { return content; }
    public Instant getSentAt() { return sentAt; }
}
