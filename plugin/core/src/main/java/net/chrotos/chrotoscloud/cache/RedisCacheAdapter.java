package net.chrotos.chrotoscloud.cache;

import lombok.NonNull;
import net.chrotos.chrotoscloud.CloudConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.params.SetParams;

import java.time.Duration;

public class RedisCacheAdapter implements CacheAdapter {
    private String host;
    private int port;
    private String password;
    private UnifiedJedis client;

    @Override
    public void configure(CloudConfig config) {
        host = config.getCacheHost();
        port = config.getCachePort();
        password = config.getCachePassword();
    }

    public void connect() {
        if (client != null) {
            return;
        }

        client = new JedisPooled(new HostAndPort(host, port), DefaultJedisClientConfig.builder()
                                    .password(password)
                                    .build());
    }

    @Override
    public void set(@NonNull String key, String value) {
        set(key, value, null);
    }

    @Override
    public void set(@NonNull String key, String value, Duration duration) {
        checkConnected();

        if (value == null) {
            client.del(key);
            return;
        }

        if (duration != null) {
            SetParams params = SetParams.setParams();

            if (duration.toMillis() % 1000 != 0) {
                params.px(duration.toMillis());
            } else {
                params.ex(duration.toSeconds());
            }

            client.set(key, value, params);
        } else {
            client.set(key, value);
        }
    }

    @Override
    public void set(@NonNull String key, Object value) {
        set(key, value, null);
    }

    @Override
    public void set(@NonNull String key, Object value, Duration duration) {
        checkConnected();

        if (value == null) {
            client.del(key);
            return;
        }

        client.jsonSet(key, value);

        if (duration != null) {
            expire(key, duration);
        }
    }

    @Override
    public String get(@NonNull String key) {
        checkConnected();

        return client.get(key);
    }

    @Override
    public <E> E get(@NonNull String key, Class<E> clazz) {
        checkConnected();

        return client.jsonGet(key, clazz);
    }

    @Override
    public void increment(@NonNull String key) {
        increment(key, null);
    }

    @Override
    public void increment(@NonNull String key, Duration duration) {
        checkConnected();

        client.incr(key);

        if (duration != null) {
            expire(key, duration);
        }
    }

    @Override
    public void decrement(@NonNull String key) {
        decrement(key, null);
    }

    @Override
    public void decrement(@NonNull String key, Duration duration) {
        checkConnected();

        client.decr(key);

        if (duration != null) {
            expire(key, duration);
        }
    }

    @Override
    public void expire(@NonNull String key, @NonNull Duration duration) {
        checkConnected();

        if (duration.toMillis() % 1000 != 0) {
            client.pexpire(key, duration.toMillis());
        } else {
            client.expire(key, duration.toSeconds());
        }
    }

    @Override
    public void persist(@NonNull String key) {
        checkConnected();

        client.persist(key);
    }

    @Override
    public Lock acquireLock(@NonNull String key) throws CacheKeyAlreadyLockedException {
        return acquireLock(key, null);
    }

    @Override
    public Lock acquireLock(@NonNull String key, Duration duration) throws CacheKeyAlreadyLockedException {
        Lock lock = acquireLockOrNull(key, duration);

        if (lock != null) {
            return lock;
        }

        throw new CacheKeyAlreadyLockedException(key);
    }

    @Override
    public Lock acquireLockOrNull(@NonNull String key) {
        return acquireLockOrNull(key, null);
    }

    @Override
    public Lock acquireLockOrNull(@NonNull String key, Duration duration) {
        checkConnected();

        if (!client.exists(key)) {
            Lock lock = createLock(key, System.nanoTime());
            set(key, String.valueOf(lock.getId()), duration);

            return lock;
        }

        return null;
    }

    private Lock createLock(String key, long id) {
        return new RedisLock(key, id, client);
    }

    @Override
    public void listPush(@NonNull String key, String... strings) {
        checkConnected();

        client.lpush(key, strings);
    }

    @Override
    public String listPop(@NonNull String key) {
        checkConnected();

        return client.lpop(key);
    }

    private void checkConnected() {
        if (client == null) {
            throw new CacheUnconnectedException();
        }
    }
}
