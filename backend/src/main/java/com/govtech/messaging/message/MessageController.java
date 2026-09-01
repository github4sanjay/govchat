package com.govtech.messaging.message;

import io.swagger.v3.oas.annotations.Operation;
import com.govtech.messaging.auth.MessagingPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messages;

    public MessageController(MessageService messages) {
        this.messages = messages;
    }

    @GetMapping
    @Operation(summary = "Get the ordered message history between two users")
    public List<MessageResponse> conversation(@AuthenticationPrincipal MessagingPrincipal currentUser,
                                              @RequestParam UUID peerId) {
        return messages.conversation(currentUser.id(), peerId);
    }
}
