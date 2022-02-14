package net.chrotos.chrotoscloud.messaging.pubsub;

@FunctionalInterface
public interface Listener {
    void onMessage(String channel, String message);
}
