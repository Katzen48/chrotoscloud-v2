package net.chrotos.chrotoscloud.economy;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Transaction {
    String getTransactionCode();
    AccountType getAccountType();
    String getAccountCode();
    UUID getFromUniqueId();
    UUID getToUniqueId();
    TransactionType getType();
    TransactionOrigin getTransactionOrigin();
    float getAmount();
    float getAbsolute();
    boolean isPositive();
    LocalDateTime getCreatedAt();
}
