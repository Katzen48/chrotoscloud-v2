package net.chrotos.chrotoscloud.messaging.pubsub;

import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class RedisPubSubAdapter implements PubSubAdapter {
    private final Supplier<JedisPooled> jedisFactory;
    private final JedisPooled client;
    private final ArrayList<Future> futures = new ArrayList<>();

    public RedisPubSubAdapter(Supplier<JedisPooled> jedisFactory) {
        this.jedisFactory = jedisFactory;
        this.client = jedisFactory.get();
    }

    @Override
    public Registration register(Listener listener, String... channels) {
        JedisPubSub pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                listener.onMessage(channel, message);
            }
        };

        Future future = CompletableFuture.runAsync(() -> {
            jedisFactory.get().subscribe(pubSub, channels);
        });

        futures.add(future);

        return new Registration() {
            @Override
            public void close() throws Exception {
                pubSub.unsubscribe();
                future.cancel(true);
                futures.remove(future);
            }

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
