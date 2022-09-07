package net.chrotos.chrotoscloud.economy;

import net.chrotos.chrotoscloud.Model;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Transaction extends Model {
    String getTransactionCode();
    AccountType getAccountType();
    UUID getAccountId();
    Account getAccount();
    UUID getFromUniqueId();
    UUID getToUniqueId();
    TransactionType getType();
    TransactionOrigin getOrigin();
    float getAmount();
    float getAbsolute();
    boolean isPositive();
    LocalDateTime getCreatedAt();
}
