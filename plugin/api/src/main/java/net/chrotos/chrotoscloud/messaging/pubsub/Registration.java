package net.chrotos.chrotoscloud.messaging.pubsub;

public interface Registration {
    void subscribe(String... channels);
    void unsubscribe(String... channels);
    boolean isSubscribed();
    Listener getListener();
}
