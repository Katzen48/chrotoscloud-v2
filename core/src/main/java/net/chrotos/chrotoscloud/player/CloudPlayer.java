package net.chrotos.chrotoscloud.player;

import lombok.Getter;
import net.chrotos.chrotoscloud.economy.Account;
import net.chrotos.chrotoscloud.economy.AccountType;
import net.chrotos.chrotoscloud.economy.CloudAccount;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity(name = "player")
@Getter
public class CloudPlayer implements Player, SoftDeletable {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID uniqueId;

    private String name;

    private transient SidedPlayer sidedPlayer;

    @OneToMany(mappedBy = "player", targetEntity = CloudAccount.class)
    private List<Account> accounts = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updatedAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar deletedAt;

    @Override
    public List<Account> getAccounts(AccountType type) {
        return getAccounts().stream().filter(
                account -> account.getAccountType() == type
        ).collect(Collectors.toList());
    }

    @Override
    public Account getAccount(UUID uniqueId) {
        return getAccounts().stream().filter(
                account -> account.getUniqueId().equals(uniqueId)
        ).findFirst().orElse(null);
    }
}
