package net.chrotos.chrotoscloud.messaging.pubsub;

public interface Registration extends AutoCloseable {
    void subscribe(String... channels);
    void unsubscribe(String... channels);
    boolean isSubscribed();
    Listener getListener();
}
