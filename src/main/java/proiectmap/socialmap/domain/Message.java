package proiectmap.socialmap.domain;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,               -- Auto-incrementing ID for the message
    sender_id BIGINT NOT NULL,              -- Foreign key referencing the User table for the sender
    receiver_ids TEXT NOT NULL,             -- A comma-separated list of receiver user IDs (or JSON format)
    content TEXT NOT NULL,                  -- The content of the message
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Timestamp when the message was sent
    reply_id BIGINT,                        -- Foreign key referencing another message if it's a reply
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,   -- Ensuring the sender exists
    FOREIGN KEY (reply_id) REFERENCES messages(id) ON DELETE CASCADE  -- Ensuring the reply message exists
);

 */
public class Message extends Entity<Long> {
    private Long id;
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime data;
    private Message reply;

    public Message(Long id, User from, List<User> to, String message, LocalDateTime data) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.reply = null;
    }

    public Message(Long id, User from, List<User> to, String message, LocalDateTime data, Message reply) {
        this(id, from, to, message, data);
        this.reply = reply;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return "From: " + (from != null ? from.getFirstName() : "null") +
                ", To: " + (to != null ? to.stream().map(User::getFirstName).collect(Collectors.joining(", ")) : "null") +
                ", Content: " + message +
                ", Timestamp: " + data;
    }
}
