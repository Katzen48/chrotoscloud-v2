package net.chrotos.chrotoscloud.persistence.mysql.cache;

import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.cache.CacheAdapter;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.*;
import java.time.Duration;
import java.util.Base64;

public class CloudStorageAccess implements DomainDataStorageAccess {
    private final String region;
    private final CacheAdapter cache = Cloud.getInstance().getCache();

    protected CloudStorageAccess(String region) {
        this.region = region;
    }

    @Override
    public Object getFromCache(Object key, SharedSessionContractImplementor session) {
        if (key == null) {
            return null;
        }

        String cacheKey = buildObjectKey(key);

        if (!cache.exists(cacheKey)) {
            return null;
        }

        return convertStringToObject(cache.get(cacheKey));
    }

    @Override
    public void putIntoCache(Object key, Object value, SharedSessionContractImplementor session) {
        if (key == null) {
            return;
        }

        cache.set(buildObjectKey(key), convertObjectToString(value), Duration.ofSeconds(120));
    }

    @Override
    public void removeFromCache(Object key, SharedSessionContractImplementor session) {
        if (key == null) {
            return;
        }

        cache.set(buildObjectKey(key), null);
    }

    @Override
    public void clearCache(SharedSessionContractImplementor session) {
        cache.keys(buildCacheKey("*")).forEach(key -> cache.set(key, null));
    }

    @Override
    public boolean contains(Object key) {
        return cache.exists(buildObjectKey(key));
    }

    @Override
    public void evictData() {
        clearCache(null);
    }

    @Override
    public void evictData(Object key) {
        removeFromCache(key, null);
    }

    @Override
    public void release() {
        evictData();
    }

    private String buildObjectKey(Object object) {
        return buildCacheKey(convertObjectToString(object));
    }

    private String buildCacheKey(String key) {
        return "hibernate:" + region + ":" + key;
    }

    private static String convertObjectToString(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream out = new ObjectOutputStream(bos)){
            out.writeObject(obj);
            out.flush();
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Object convertStringToObject(String str) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(str.getBytes())); ObjectInput in = new ObjectInputStream(bis)){
            return in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
