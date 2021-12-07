package net.chrotos.chrotoscloud.economy;

import java.util.Collection;
import java.util.UUID;

public interface AccountHolder {
    Collection<Account> getAccounts();
    Collection<Account> getAccounts(AccountType type);
    Account getAccount(UUID uniqueId);
}
