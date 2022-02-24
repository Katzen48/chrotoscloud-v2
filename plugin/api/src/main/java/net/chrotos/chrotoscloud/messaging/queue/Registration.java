package net.chrotos.chrotoscloud.messaging.queue;

import net.chrotos.chrotoscloud.messaging.queue.Listener;

public interface Registration<E,T> {
    void unsubscribe();
    boolean isSubscribed();
    Listener<E,T> getListener();
}
