package proiectmap.socialmap.utils.events;

import proiectmap.socialmap.domain.User;

public class UserEvent implements Event {

    private ChangeEventType type;
    private User newUser, oldUser;

    public UserEvent(ChangeEventType type, User newUser) {
        this.type = type;
        this.newUser = newUser;
    }

    public UserEvent(ChangeEventType type, User newUser, User oldUser) {
        this.type = type;
        this.newUser = newUser;
        this.oldUser = oldUser;
    }

    public ChangeEventType getType() {
        return type;
    }

    public User getNewUser() {
        return newUser;
    }

    public User getOldUser() {
        return oldUser;
    }
}
