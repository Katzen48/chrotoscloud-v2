package net.chrotos.chrotoscloud.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CacheKeyAlreadyLockedException extends RuntimeException {
    private final String key;
}
