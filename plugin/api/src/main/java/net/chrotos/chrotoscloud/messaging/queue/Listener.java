package net.chrotos.chrotoscloud.messaging.queue;

public interface Listener<E,T> {
    void onMessage(Message<E> object, String sender);
    void onReply(Message<T> object, String sender);
    Class<T> getReplyClass();
    Class<E> getMessageClass();
}
