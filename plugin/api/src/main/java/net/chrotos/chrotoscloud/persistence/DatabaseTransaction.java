package net.chrotos.chrotoscloud.persistence;

public interface DatabaseTransaction {
    void commit();
    void rollback();
}
