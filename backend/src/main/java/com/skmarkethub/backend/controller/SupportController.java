package com.skmarkethub.backend.controller;

import com.skmarkethub.backend.entity.User;
import com.skmarkethub.backend.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final UserRepository userRepository;
    private final Map<String, Map<String, Object>> tickets = new ConcurrentHashMap<>();

    public SupportController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public record CreateTicketRequest(@NotBlank String subject, @NotBlank String message) {}
    public record ReplyTicketRequest(@NotBlank String reply) {}

    @GetMapping("/tickets")
    public List<Map<String, Object>> getTickets(@AuthenticationPrincipal Jwt jwt) {
        return tickets.values().stream().toList();
    }

    @PostMapping("/tickets")
    public ResponseEntity<Map<String, Object>> createTicket(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateTicketRequest request) {

        User user = userRepository.findByEmailIgnoreCase(jwt.getSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        String ticketId = "TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Map<String, Object> ticket = new LinkedHashMap<>();
        ticket.put("ticketId", ticketId);
        ticket.put("userId", user.getId());
        ticket.put("userEmail", user.getEmail());
        ticket.put("subject", request.subject().trim());
        ticket.put("message", request.message().trim());
        ticket.put("status", "OPEN");
        ticket.put("createdAt", LocalDateTime.now());
        ticket.put("replies", new ArrayList<Map<String, Object>>());

        tickets.put(ticketId, ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @PostMapping("/tickets/{ticketId}/reply")
    public ResponseEntity<Map<String, Object>> replyTicket(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String ticketId,
            @Valid @RequestBody ReplyTicketRequest request) {

        Map<String, Object> ticket = tickets.get(ticketId);
        if (ticket == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> replies = (List<Map<String, Object>>) ticket.get("replies");

        Map<String, Object> reply = new LinkedHashMap<>();
        reply.put("sender", jwt.getSubject());
        reply.put("reply", request.reply().trim());
        reply.put("timestamp", LocalDateTime.now());
        replies.add(reply);

        ticket.put("status", "IN_PROGRESS");
        return ResponseEntity.ok(ticket);
    }
}
