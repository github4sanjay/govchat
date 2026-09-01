package com.govtech.messaging.user;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    @GetMapping
    @Operation(summary = "List users available for the MVP demo")
    public List<UserResponse> list() {
        return users.findAll(Sort.by("displayName")).stream().map(UserResponse::from).toList();
    }

    public record UserResponse(UUID id, String username, String displayName) {
        static UserResponse from(User user) {
            return new UserResponse(user.getId(), user.getUsername(), user.getDisplayName());
        }
    }
}
