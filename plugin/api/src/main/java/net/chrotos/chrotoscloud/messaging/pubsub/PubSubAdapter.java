package net.chrotos.chrotoscloud.messaging.pubsub;

public interface PubSubAdapter {
    Registration register(Listener listener, String... channels);
    void publish(String channel, String message);
}
