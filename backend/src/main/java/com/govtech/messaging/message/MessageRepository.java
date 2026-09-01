package com.govtech.messaging.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    Optional<Message> findBySenderIdAndClientMessageId(UUID senderId, UUID clientMessageId);

    @Query("""
            select m from Message m
            join fetch m.sender
            join fetch m.recipient
            where (m.sender.id = :first and m.recipient.id = :second)
               or (m.sender.id = :second and m.recipient.id = :first)
            order by m.sentAt asc, m.id asc
            """)
    List<Message> findConversation(@Param("first") UUID first, @Param("second") UUID second);
}
