package proiectmap.socialmap.utils;

import proiectmap.socialmap.utils.Observer;
import proiectmap.socialmap.utils.events.FriendshipEvent;

public interface FriendshipObservable {
    void addObserverFriendship(Observer<FriendshipEvent> e);
    void removeObserverFrienship(Observer<FriendshipEvent> e);
    void notifyObserversFriendship(FriendshipEvent t);
}