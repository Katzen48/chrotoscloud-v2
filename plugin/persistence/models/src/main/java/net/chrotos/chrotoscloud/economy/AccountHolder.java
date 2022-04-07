package net.chrotos.chrotoscloud.economy;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface AccountHolder {
    UUID getUniqueId();
    Set<Account> getAccounts();
    Collection<? extends Account> getAccounts(AccountType type);
    Account getAccount(UUID uniqueId);
}
