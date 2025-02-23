package proiectmap.socialmap.utils;

import proiectmap.socialmap.utils.Observer;
import proiectmap.socialmap.utils.events.MessageEvent;

public interface MessageObservable {

    void addObserverMessage(Observer<MessageEvent> e);

    void removeObserverMessage(Observer<MessageEvent> e);

    void notifyObserversMessage(MessageEvent t);
}