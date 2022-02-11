package net.chrotos.chrotoscloud.messaging.pubsub;

public interface Listener {
    void onMessage(String channel, String message);
}
