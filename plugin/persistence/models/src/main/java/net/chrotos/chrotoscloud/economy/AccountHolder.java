package net.chrotos.chrotoscloud.economy;

import java.util.List;
import java.util.UUID;

public interface AccountHolder {
    UUID getUniqueId();
    List<Account> getAccounts();
    List<Account> getAccounts(AccountType type);
    Account getAccount(UUID uniqueId);
}
