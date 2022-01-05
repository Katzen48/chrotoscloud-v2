package net.chrotos.chrotoscloud.persistence;

@FunctionalInterface
public interface TransactionRunnable {
    void run(DatabaseTransaction databaseTransaction);
}
