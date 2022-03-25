package net.chrotos.chrotoscloud.messaging.queue;

import lombok.NonNull;

import java.io.IOException;

public interface Message<E> {
    @NonNull
    E getMessage();
    void replyTo(@NonNull Object object) throws IOException;
}
