package proiectmap.socialmap.domain;

import java.time.LocalDateTime;
import java.util.List;

public class ReplyMessage extends Message{
    private Message originalMessage;

    public ReplyMessage(Long id, User from, List<User> to, String message, LocalDateTime data, Message originalMessage) {
        super(id, from, to, message, data);
        this.originalMessage = originalMessage;
    }

    public Message getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(Message originalMessage) {
        this.originalMessage = originalMessage;
    }

    @Override
    public String toString() {
        return super.toString() + " [In reply to: " + originalMessage.getMessage() + "]";
    }
}
