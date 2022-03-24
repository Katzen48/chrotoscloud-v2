package net.chrotos.chrotoscloud.messaging.queue;

import lombok.NonNull;

public interface Listener<E,T> {
    void onMessage(@NonNull Message<E> object, @NonNull String sender);
    void onReply(@NonNull Message<T> object, @NonNull String sender);
    Class<T> getReplyClass();
    @NonNull
    Class<E> getMessageClass();
}
