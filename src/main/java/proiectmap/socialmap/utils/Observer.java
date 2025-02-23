package proiectmap.socialmap.utils;


import proiectmap.socialmap.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}
