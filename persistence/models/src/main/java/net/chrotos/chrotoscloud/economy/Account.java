package net.chrotos.chrotoscloud.economy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

public interface Account {
    UUID getOwnerId();
    AccountType getAccountType();
    String getAccountCode();
    Collection<Transaction> getTransactions();
    float getBalance();
    float getLimit();
    float getCreditLimit();
    boolean isSharedAccount();
    Collection<UUID> getSharedWith();
    LocalDateTime getCreatedAt();
}
