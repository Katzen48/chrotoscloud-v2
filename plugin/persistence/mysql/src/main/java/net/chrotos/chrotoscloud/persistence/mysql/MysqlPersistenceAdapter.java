package net.chrotos.chrotoscloud.persistence.mysql;

import net.chrotos.chrotoscloud.CloudConfig;
import net.chrotos.chrotoscloud.persistence.*;
import org.flywaydb.core.Flyway;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class MysqlPersistenceAdapter implements PersistenceAdapter {
    private SessionFactory sessionFactory;
    private final ThreadLocal<Session> sessionThreadLocal = ThreadLocal.withInitial(this::supplySession);

    @Override
    public boolean isConnected() {
        return sessionFactory != null;
    }

    @Override
    public boolean configure(CloudConfig config) {
        try {
            if (config.shouldRunMigrations()) {
                Flyway flyway = Flyway.configure().dataSource(  config.getPersistenceConnectionString(),
                                                                config.getPersistenceUser(),
                                                                config.getPersistencePassword())
                                                    .table("migrations")
                                                    .load();

                // TODO add logging
                flyway.migrate();
            }

            Configuration dbConfig = new Configuration()
                    .configure("/net/chrotos/chrotoscloud/persistence/mysql/hibernate.cfg.xml")
                    .setProperty("hibernate.connection.url", config.getPersistenceConnectionString())
                    .setProperty("hibernate.connection.username", config.getPersistenceUser())
                    .setProperty("hibernate.connection.password", config.getPersistencePassword());

            dbConfig.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());

            sessionFactory = dbConfig.buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public <E> List<E> getAll(Class<E> clazz) {
        return getAll(clazz, DataSelectFilter.builder().build());
    }

    @Override
    public <E> List<E> getAll(Class<E> clazz, DataSelectFilter filter) {
        Session session = getSession();
        boolean insideTransation = session.getTransaction().isActive();
        if (!insideTransation) {
            session.beginTransaction();
        }

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(clazz);
        Root<E> rootEntry = cq.from(clazz);
        CriteriaQuery<E> all = cq.select(rootEntry);

        for (Map.Entry<String, Object> entry : filter.getColumnFilters().entrySet()) {
            all = all.where(cb.equal(rootEntry.get(entry.getKey()), entry.getValue()));
        }

        if (filter.getOrderKey() != null) {
            Order order;
            if (filter.getOrdering() == Ordering.ASCENDING) {
                order = cb.asc(rootEntry.get(filter.getOrderKey()));
            } else {
                order = cb.desc(rootEntry.get(filter.getOrderKey()));
            }

            all = all.orderBy(order);
        }

        TypedQuery<E> allQuery = session.createQuery(all);

        List<E> list = allQuery.getResultList();

        if (!insideTransation) {
            session.getTransaction().commit();
        }

        return list;
    }

    @Override
    public <E> List<E> getFiltered(Class<E> clazz, String predefinedFilter, Map<String, Object> parameters) {
        return getFiltered(clazz, predefinedFilter, parameters, DataSelectFilter.builder().build());
    }

    @Override
    public <E> List<E> getFiltered(Class<E> clazz, String predefinedFilter, Map<String, Object> parameters,
                                   DataSelectFilter dataSelectFilter) {
        Session session = getSession();
        boolean insideTransation = session.getTransaction().isActive();
        if (!insideTransation) {
            session.beginTransaction();
        }

        List<E> list;

        Filter filter = session.enableFilter(predefinedFilter);

        try {
            parameters.forEach(filter::setParameter);
            list = getAll(clazz, dataSelectFilter);
        } catch (Exception e) {
            e.printStackTrace();
            list = Collections.emptyList();
        }

        session.disableFilter(predefinedFilter);

        if (!insideTransation) {
            session.getTransaction().commit();
        }

        return list;
    }

    @Override
    public <E> E executeFiltered(String predefinedFilter, Map<String, Object> parameters, Supplier<E> supplier) {
        Session session = getSession();
        Filter filter = session.enableFilter(predefinedFilter);

        E result;
        try {
            result = supplier.get();
        } catch (Exception e) {
            session.disableFilter(predefinedFilter);
            throw e;
        }
        session.disableFilter(predefinedFilter);

        return result;
    }

    @Override
    public <E> E getOne(Class<E> clazz, DataSelectFilter filter) {
        if (filter.getPrimaryKeyValue() == null) {
            throw new IllegalArgumentException("A primary Key Value needs to be defined");
        }

        Session session = getSession();
        boolean insideTransaction = session.getTransaction().isActive();
        if (!insideTransaction) {
            session.beginTransaction();
        }

        E entity = session.find(clazz, filter.getPrimaryKeyValue());

        if (entity != null) {
            refresh(entity);
        }

        if (!insideTransaction) {
            session.getTransaction().commit();
        }

        return entity;
    }

    @Override
    public <E> void save(E entity) throws net.chrotos.chrotoscloud.persistence.EntityExistsException {
        Session session = getSession();

        try {
            runInTransaction((databaseTransaction) -> session.saveOrUpdate(entity));
        } catch (PersistenceException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new net.chrotos.chrotoscloud.persistence.EntityExistsException(entity);
            }

            throw e;
        }
    }

    @Override
    public void removeFromContext(Object object) {
        Session session = getSession();

        if (session.contains(object)) {
            session.detach(object);
        }
    }

    @Override
    public void runInTransaction(TransactionRunnable runnable) {
        Session session = getSession();

        EntityTransaction transaction = session.getTransaction();
        boolean insideTransaction = transaction.isActive();

        if (!insideTransaction) {
            transaction.begin();
        }

        final AtomicBoolean noCommit = new AtomicBoolean(false);
        try {
            runnable.run(new DatabaseTransaction() {
                @Override
                public void commit() {
                    transaction.commit();
                    transaction.begin();
                }

                @Override
                public void rollback() {
                    transaction.rollback();
                }

                @Override
                public void setSuppressCommit(boolean suppressCommit) {
                    noCommit.set(suppressCommit);
                }

                @Override
                public void suppressCommit() {
                    setSuppressCommit(true);
                }
            });

            if (!insideTransaction && !noCommit.get()) {
                transaction.commit();
            }
        } catch (Exception e) {
            transaction.rollback();

            throw e;
        }
    }

    @Override
    public void refresh(Object object) {
        Session session = getSession();
        runInTransaction((databaseTransaction -> session.refresh(object)));
    }

    @Override
    public void merge(Object object) {
        Session session = getSession();
        runInTransaction((databaseTransaction) -> session.saveOrUpdate(object));
    }

    private Session getSession() {
        Session session = sessionThreadLocal.get();

        if (!session.isOpen() || !session.isConnected()) {
            sessionThreadLocal.remove();
            return getSession();
        }

        return session;
    }

    private Session supplySession() {
        return sessionFactory.openSession();
    }
}
