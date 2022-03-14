package net.chrotos.chrotoscloud.persistence.mysql;

import net.chrotos.chrotoscloud.CloudConfig;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;
import net.chrotos.chrotoscloud.persistence.DatabaseTransaction;
import net.chrotos.chrotoscloud.persistence.PersistenceAdapter;
import net.chrotos.chrotoscloud.persistence.TransactionRunnable;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

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
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(clazz);
        Root<E> rootEntry = cq.from(clazz);
        CriteriaQuery<E> all = cq.select(rootEntry);
        TypedQuery<E> allQuery = entityManager.createQuery(all);

        // TODO apply order

        return allQuery.getResultList();
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

        return getEntityManager().find(clazz, filter.getPrimaryKeyValue());
    }

    @Override
    public <E> void save(E entity) throws net.chrotos.chrotoscloud.persistence.EntityExistsException {
        EntityManager entityManager = getEntityManager();

        if (entityManager.contains(entity)) {
            return;
        }

        try {
            if (entityManager.getTransaction().isActive()) {
                entityManager.persist(entity);
            } else {
                runInTransaction((databaseTransaction) -> entityManager.persist(entity));
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
        EntityManager entityManager = getEntityManager();

        if (entityManager.contains(object)) {
            entityManager.detach(object);
        }
    }

    @Override
    public void runInTransaction(TransactionRunnable runnable) {
        EntityManager entityManager = getEntityManager();

        EntityTransaction transaction = entityManager.getTransaction();
        boolean insideTransaction = transaction.isActive();

        if (!insideTransaction) {
            transaction.begin();
        }

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
            });

            if (!insideTransaction) {
                transaction.commit();
            }
        } catch (Exception e) {
            transaction.rollback();

            throw e;
        }
    }

    @Override
    public void refresh(Object object) {
        EntityManager entityManager = getEntityManager();

        if (entityManager.contains(object)) {
            entityManager.refresh(object);
        }
    }

    @Override
    public void merge(Object object) {
        EntityManager entityManager = getEntityManager();

        if (entityManager.getTransaction().isActive()) {
            entityManager.merge(object);
        } else {
            runInTransaction((databaseTransaction) -> entityManager.merge(object));
        }
    }

    private EntityManager getEntityManager() {
        return sessionFactory.createEntityManager();
    }
}
