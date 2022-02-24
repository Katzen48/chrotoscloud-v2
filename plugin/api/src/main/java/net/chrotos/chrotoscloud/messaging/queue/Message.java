package net.chrotos.chrotoscloud.messaging.queue;

import lombok.NonNull;

public interface Message<E> {
    E getMessage();
    void replyTo(@NonNull Object object);
}
