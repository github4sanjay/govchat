package com.govtech.messaging.auth;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository contexts;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, SecurityContextRepository contexts,
                          AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.contexts = contexts;
        this.authService = authService;
    }

    @GetMapping("/csrf")
    @Operation(summary = "Create a CSRF token for state-changing authentication requests")
    CsrfResponse csrf(CsrfToken token) {
        return new CsrfResponse(token.getToken(), token.getHeaderName());
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and create an HTTP session")
    AuthUserResponse login(@Valid @RequestBody LoginRequest loginRequest,
                           HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        loginRequest.username().trim().toLowerCase(), loginRequest.password()));

        if (request.getSession(false) != null) {
            request.changeSessionId();
        }
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        contexts.saveContext(context, request, response);
        return AuthUserResponse.from((MessagingPrincipal) authentication.getPrincipal());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a local account; login is a separate explicit step")
    AuthUserResponse register(@Valid @RequestBody RegisterRequest request) {
        return AuthUserResponse.from(authService.register(request.username(), request.displayName(), request.password()));
    }

    @GetMapping("/me")
    @Operation(summary = "Return the currently authenticated user")
    AuthUserResponse me(@AuthenticationPrincipal MessagingPrincipal principal) {
        return AuthUserResponse.from(principal);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    AuthError invalidCredentials() {
        // A generic message avoids revealing whether a username exists.
        return new AuthError("INVALID_CREDENTIALS", "Invalid username or password");
    }

    @ExceptionHandler(AuthService.UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    AuthError usernameExists(AuthService.UsernameAlreadyExistsException exception) {
        return new AuthError("USERNAME_EXISTS", exception.getMessage());
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record RegisterRequest(@NotBlank String username, @NotBlank String displayName, @NotBlank String password) {}
    public record CsrfResponse(String token, String headerName) {}
    public record AuthError(String code, String message) {}
    public record AuthUserResponse(java.util.UUID id, String username, String displayName) {
        static AuthUserResponse from(MessagingPrincipal principal) {
            return new AuthUserResponse(principal.id(), principal.getUsername(), principal.displayName());
        }
    }
}
