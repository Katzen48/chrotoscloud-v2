package net.chrotos.chrotoscloud.persistence;

import net.chrotos.chrotoscloud.CloudConfig;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface PersistenceAdapter {
    boolean isConnected();
    boolean configure(CloudConfig config);
    <E> List<E> getAll(Class<E> clazz);
    <E> List<E> getAll(Class<E> clazz, DataSelectFilter filter);
    <E> List<E> getFiltered(Class<E> clazz, String predefinedFilter, Map<String, Object> parameters);
    <E> List<E> getFiltered(Class<E> clazz, String predefinedFilter, Map<String, Object> parameters, DataSelectFilter filter);
    <E> E executeFiltered(String predefinedFilter, Map<String, Object> parameters, Supplier<E> supplier);
    <E> E getOne(Class<E> clazz, DataSelectFilter filter);
    <E> void save(E entity) throws EntityExistsException;
    void removeFromContext(Object object);
    void runInTransaction(TransactionRunnable runnable);
    void refresh(Object object);
    void merge(Object object);
}
