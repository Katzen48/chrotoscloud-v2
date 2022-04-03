package net.chrotos.chrotoscloud.persistence;

public interface DatabaseTransaction {
    void commit();
    void rollback();
    void setSuppressCommit(boolean suppressCommit);
    void suppressCommit();
}
