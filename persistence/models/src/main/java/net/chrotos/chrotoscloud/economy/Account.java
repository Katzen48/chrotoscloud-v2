package net.chrotos.chrotoscloud.economy;

import net.chrotos.chrotoscloud.player.Player;

import java.util.Collection;
import java.util.UUID;

public interface Account {
    UUID getUniqueId();
    UUID getOwnerId();
    Player getOwner();
    AccountType getAccountType();
    Collection<Transaction> getTransactions();
    float getBalance();
    float getLimit();
    float getCreditLimit();
    boolean isSharedAccount();
    Collection<UUID> getSharedWith();
}
