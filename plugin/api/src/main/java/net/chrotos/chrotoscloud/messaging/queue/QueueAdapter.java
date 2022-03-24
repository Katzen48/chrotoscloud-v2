package net.chrotos.chrotoscloud.messaging.queue;

import lombok.NonNull;

import java.io.IOException;

public interface QueueAdapter {
    void initialize();
    <E,T> Registration<E,T> register(@NonNull Listener<E,T> listener, @NonNull String channel) throws IOException;
    <E> void publish(@NonNull String channel, @NonNull E object);
    <E> void publish(@NonNull String channel, @NonNull String routingKey, @NonNull E object);
}
