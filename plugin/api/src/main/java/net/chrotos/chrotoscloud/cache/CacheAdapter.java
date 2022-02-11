package net.chrotos.chrotoscloud.cache;

import lombok.NonNull;
import net.chrotos.chrotoscloud.CloudConfig;

import java.time.Duration;
import java.util.Set;

public interface CacheAdapter {
    void configure(CloudConfig config);

    void set(@NonNull String key, String value);
    void set(@NonNull String key, String value, Duration duration);
    void set(@NonNull String key, Object value);
    void set(@NonNull String key, Object value, Duration duration);

    boolean exists(@NonNull String key);

    String get(@NonNull String key);
    <E> E get(@NonNull String key, Class<E> clazz);

    void increment(@NonNull String key);
    void increment(@NonNull String key, Duration duration);

    void decrement(@NonNull String key);
    void decrement(@NonNull String key, Duration duration);

    void expire(@NonNull String key, @NonNull Duration duration);

    void persist(@NonNull String key);

    Lock acquireLock(@NonNull String key) throws CacheKeyAlreadyLockedException;
    Lock acquireLock(@NonNull String key, Duration duration) throws CacheKeyAlreadyLockedException;
    Lock acquireLockOrNull(@NonNull String key);
    Lock acquireLockOrNull(@NonNull String key, Duration duration);

    void listPush(@NonNull String key, String... strings);
    String listPop(@NonNull String key);

    void setAdd(@NonNull String key, @NonNull String... values);
    void setRemove(@NonNull String key, @NonNull String... values);
    boolean setContains(@NonNull String key, @NonNull String value);
    long setSize(@NonNull String key);
    Set<String> setMembers(@NonNull String key);

    Set<String> keys(@NonNull String pattern);
}
