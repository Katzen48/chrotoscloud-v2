package net.chrotos.chrotoscloud.cache;

public interface Lock {
    String getKey();
    long getId();
    void release();
}
