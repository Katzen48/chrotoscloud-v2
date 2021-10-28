package net.chrotos.chrotoscloud.economy;

import java.util.Collection;

public interface AccountHolder {
    Collection<Account> getAccounts();
    Collection<Account> getAccounts(AccountType type);
    Collection<Account> getAccounts(String accountCode);
    Account getAccount(AccountType type, String accountCode);
}
