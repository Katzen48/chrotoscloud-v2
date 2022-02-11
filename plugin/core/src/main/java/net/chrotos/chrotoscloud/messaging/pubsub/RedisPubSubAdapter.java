package net.chrotos.chrotoscloud.messaging.pubsub;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.UnifiedJedis;

@RequiredArgsConstructor
public class RedisPubSubAdapter implements PubSubAdapter {
    private final UnifiedJedis client;

    @Override
    public Registration register(Listener listener, String... channels) {
        JedisPubSub pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                listener.onMessage(channel, message);
            }
        };

        client.subscribe(pubSub, channels);

        return new Registration() {
            @Override
            public void subscribe(String... channels) {
                pubSub.subscribe(channels);
            }

            @Override
            public void unsubscribe(String... channels) {
                pubSub.unsubscribe(channels);
            }

            @Override
            public boolean isSubscribed() {
                return pubSub.isSubscribed();
            }

            @Override
            public Listener getListener() {
                return listener;
            }
        };
    }

    @Override
    public void publish(String channel, String message) {
        client.publish(channel, message);
    }
}
