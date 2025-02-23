package proiectmap.socialmap.utils.events;

import proiectmap.socialmap.domain.Friendship;

public class FriendshipEvent implements Event {

    private ChangeEventType type;
    private Friendship newFriendship;
    private Friendship oldFriendship;

    // Constructor for event where only a new friendship is involved (e.g., adding a new friendship)
    public FriendshipEvent(ChangeEventType type, Friendship newFriendship) {
        this.type = type;
        this.newFriendship = newFriendship;
    }

    // Constructor for event where both old and new friendships are involved (e.g., updating or removing a friendship)
    public FriendshipEvent(ChangeEventType type, Friendship newFriendship, Friendship oldFriendship) {
        this.type = type;
        this.newFriendship = newFriendship;
        this.oldFriendship = oldFriendship;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Friendship getNewFriendship() {
        return newFriendship;
    }

    public Friendship getOldFriendship() {
        return oldFriendship;
    }
}