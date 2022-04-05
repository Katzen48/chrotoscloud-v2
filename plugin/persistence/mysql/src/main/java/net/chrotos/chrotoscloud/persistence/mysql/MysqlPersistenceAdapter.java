package net.chrotos.chrotoscloud.persistence.mysql;

import net.chrotos.chrotoscloud.CloudConfig;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;
import net.chrotos.chrotoscloud.persistence.DatabaseTransaction;
import net.chrotos.chrotoscloud.persistence.PersistenceAdapter;
import net.chrotos.chrotoscloud.persistence.TransactionRunnable;
import org.flywaydb.core.Flyway;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MysqlPersistenceAdapter implements PersistenceAdapter {
    private SessionFactory sessionFactory;

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
        Session session = getSession();
        if (!session.getTransaction().isActive()) {
            session.beginTransaction();
        }

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(clazz);
        Root<E> rootEntry = cq.from(clazz);
        CriteriaQuery<E> all = cq.select(rootEntry);
        TypedQuery<E> allQuery = session.createQuery(all);

        // TODO apply order

        List<E> list = allQuery.getResultList();
        list.forEach(this::refresh);

        return list;
    }

    @Override
    public <E> List<E> getAll(Class<E> clazz, DataSelectFilter filter) {
        return Collections.emptyList(); // TODO
    }

    @Override
    public <E> E getOne(Class<E> clazz, DataSelectFilter filter) {
        if (filter.getPrimaryKeyValue() == null) {
            throw new IllegalArgumentException("A primary Key Value needs to be defined");
        }

        Session session = getSession();
        if (!session.getTransaction().isActive()) {
            session.beginTransaction();
        }

        E entity = session.find(clazz, filter.getPrimaryKeyValue());
        refresh(entity);

        return entity;
    }

    @Override
    public <E> void save(E entity) throws net.chrotos.chrotoscloud.persistence.EntityExistsException {
        Session session = getSession();

        try {
            if (session.getTransaction().isActive()) {
                session.saveOrUpdate(entity);
            } else {
                runInTransaction((databaseTransaction) -> session.saveOrUpdate(entity));
            }
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
        session.beginTransaction();

        if (session.contains(object)) {
            session.refresh(object);
        }
    }

    @Override
    public void merge(Object object) {
        Session session = getSession();
        session.beginTransaction();

        if (session.getTransaction().isActive()) {
            session.saveOrUpdate(object);
        } else {
            runInTransaction((databaseTransaction) -> session.saveOrUpdate(object));
        }
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
