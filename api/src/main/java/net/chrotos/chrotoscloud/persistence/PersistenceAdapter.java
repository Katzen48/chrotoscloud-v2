package net.chrotos.chrotoscloud.persistence;

import net.chrotos.chrotoscloud.CloudConfig;

import java.util.Collection;

public interface PersistenceAdapter {
    boolean isConnected();
    boolean configure(CloudConfig config);
    <E> Collection<E> getAll(Class<E> clazz);
    <E> Collection<E> getAll(Class<E> clazz, DataSelectFilter filter);
    <E> E getOne(Class<E> clazz, DataSelectFilter filter);
}
