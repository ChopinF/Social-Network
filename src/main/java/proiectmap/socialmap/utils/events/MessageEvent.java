package proiectmap.socialmap.utils.events;

import proiectmap.socialmap.domain.Message;

public class MessageEvent implements Event {

    private ChangeEventType type;
    private Message newMessage;
    private Message oldMessage;

    /**
     * Constructor for events with only a new message.
     *
     * @param type       The type of change event (ADD, UPDATE, DELETE).
     * @param newMessage The new message involved in the event.
     */
    public MessageEvent(ChangeEventType type, Message newMessage) {
        this.type = type;
        this.newMessage = newMessage;
    }

    /**
     * Constructor for events involving both a new and old message.
     *
     * @param type       The type of change event (ADD, UPDATE, DELETE).
     * @param newMessage The new message involved in the event.
     * @param oldMessage The old message involved in the event (if applicable).
     */
    public MessageEvent(ChangeEventType type, Message newMessage, Message oldMessage) {
        this.type = type;
        this.newMessage = newMessage;
        this.oldMessage = oldMessage;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Message getNewMessage() {
        return newMessage;
    }

    public Message getOldMessage() {
        return oldMessage;
    }
}