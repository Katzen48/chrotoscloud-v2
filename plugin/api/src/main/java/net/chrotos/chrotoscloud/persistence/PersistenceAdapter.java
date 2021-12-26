package net.chrotos.chrotoscloud.persistence;

import net.chrotos.chrotoscloud.CloudConfig;

import java.util.List;

public interface PersistenceAdapter {
    boolean isConnected();
    boolean configure(CloudConfig config);
    <E> List<E> getAll(Class<E> clazz);
    <E> List<E> getAll(Class<E> clazz, DataSelectFilter filter);
    <E> E getOne(Class<E> clazz, DataSelectFilter filter);
    <E> void save(E entity);
    void removeFromContext(Object object);
    void runInTransaction(Runnable runnable);
}
