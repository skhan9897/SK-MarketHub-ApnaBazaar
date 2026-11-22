package com.sk.markethub.model;

import java.util.List;

public class Ticket {
    private String ticketId;
    private Long userId;
    private String userEmail;
    private String subject;
    private String message;
    private String status;
    private String createdAt;
    private List<Reply> replies;

    public static class Reply {
        private String sender;
        private String reply;
        private String timestamp;

        public String getSender() { return sender; }
        public String getReply() { return reply; }
        public String getTimestamp() { return timestamp; }
    }

    public Ticket() {}

    public String getTicketId() { return ticketId; }
    public Long getUserId() { return userId; }
    public String getUserEmail() { return userEmail; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public List<Reply> getReplies() { return replies; }
}
