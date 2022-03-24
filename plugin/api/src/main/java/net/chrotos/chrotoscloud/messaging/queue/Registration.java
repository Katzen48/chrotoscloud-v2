package net.chrotos.chrotoscloud.messaging.queue;

import lombok.NonNull;

import java.io.IOException;

public interface Registration<E,T> extends AutoCloseable {
    void unsubscribe();
    boolean isSubscribed();
    boolean isConnected();
    Listener<E,T> getListener();
    boolean publish(@NonNull Object message) throws IOException;
}
