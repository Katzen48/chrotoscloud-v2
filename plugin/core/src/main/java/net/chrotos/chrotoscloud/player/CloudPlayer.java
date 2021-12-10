package net.chrotos.chrotoscloud.player;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.economy.Account;
import net.chrotos.chrotoscloud.economy.AccountType;
import net.chrotos.chrotoscloud.economy.CloudAccount;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity(name = "players")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class CloudPlayer implements Player, SoftDeletable {
    @Id
    @Column(updatable = false, nullable = false)
    @NonNull
    @Type(type = "uuid-char")
    private UUID uniqueId;

    @NonNull
    private String name;

    private transient SidedPlayer sidedPlayer;

    @OneToMany(mappedBy = "owner", targetEntity = CloudAccount.class, fetch = FetchType.LAZY)
    private Collection<Account> accounts = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdAt = Calendar.getInstance();
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updatedAt = Calendar.getInstance();
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
