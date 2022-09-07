package net.chrotos.chrotoscloud.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.UnifiedJedis;

import java.time.Duration;

@RequiredArgsConstructor
public class RedisLock implements Lock {
    @Getter
    private final String key;
    @Getter
    private final long id;
    private final UnifiedJedis client;

    @Override
    public void release() {
        client.del(key);
    }

    @Override
    public void extend(Duration duration) {
        client.expire(key, duration.toSeconds());
    }
}
