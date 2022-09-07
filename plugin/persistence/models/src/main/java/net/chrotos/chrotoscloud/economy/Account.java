package net.chrotos.chrotoscloud.economy;

import net.chrotos.chrotoscloud.Model;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface Account extends Model {
    UUID getUniqueId();
    AccountHolder getOwner();
    AccountType getAccountType();
    Set<Transaction> getTransactions();
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
    void addTransaction(Transaction transaction);
}
