package net.chrotos.chrotoscloud.economy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface Account {

    UUID getUniqueId();
    AccountHolder getOwner();
    AccountType getAccountType();
    List<Transaction> getTransactions();
    float getBalance();
    float getBalanceLimit();
    float getCreditLimit();
    boolean isSharedAccount();

    /*
     * Transactions
     */
    void addTransaction(TransactionOrigin origin, float amount);
    void addTransaction(TransactionOrigin origin, float amount, Account source);
    void addTransaction(TransactionOrigin origin, float amount, LocalDateTime createdAt);
    void addTransaction(TransactionOrigin origin, float amount, LocalDateTime createdAt, Account source);
    void addTransaction(TransactionOrigin origin, float amount, LocalDateTime createdAt, String transactionCode);
    void addTransaction(TransactionOrigin origin, float amount, LocalDateTime createdAt, String transactionCode, Account source);
}
