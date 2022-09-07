package net.chrotos.chrotoscloud.cache;

import java.time.Duration;

public interface Lock {
    String getKey();
    long getId();
    void release();
    void extend(Duration duration);
}
