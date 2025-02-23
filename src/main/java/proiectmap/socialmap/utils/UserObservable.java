package proiectmap.socialmap.utils;
import proiectmap.socialmap.utils.Observer;
import proiectmap.socialmap.utils.events.UserEvent;

public interface UserObservable {
    void addObserverUser(Observer<UserEvent> e);
    void removeObserverUser(Observer<UserEvent> e);
    void notifyObserversUser(UserEvent t);
}