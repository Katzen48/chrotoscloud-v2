package net.chrotos.chrotoscloud.economy;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Data
public class CloudAccount implements Account {
    private UUID ownerId;
    private AccountType accountType;
    private String accountCode;
    private float balance;
    private float limit;
    private float creditLimit;
    private boolean sharedAccount;
    private LocalDateTime createdAt;

    @Override
    public Collection<Transaction> getTransactions() {
        return null;
    }

    @Override
    public Collection<UUID> getSharedWith() {
        return null;
    }
}
