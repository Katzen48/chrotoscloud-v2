package net.chrotos.chrotoscloud.economy;

import net.chrotos.chrotoscloud.Model;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface AccountHolder extends Model {
    UUID getUniqueId();
    Set<? extends Account> getAccounts();
    Collection<? extends Account> getAccounts(AccountType type);
    Account getAccount(UUID uniqueId);
}
