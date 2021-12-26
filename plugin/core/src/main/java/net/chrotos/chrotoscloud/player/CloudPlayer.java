package net.chrotos.chrotoscloud.player;

import lombok.*;
import net.chrotos.chrotoscloud.economy.Account;
import net.chrotos.chrotoscloud.economy.AccountType;
import net.chrotos.chrotoscloud.economy.CloudAccount;
import net.chrotos.chrotoscloud.permissions.CloudPermissible;
import net.chrotos.chrotoscloud.permissions.CloudPermission;
import net.chrotos.chrotoscloud.permissions.Permission;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity(name = "players")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@DynamicUpdate
public class CloudPlayer extends CloudPermissible implements Player, SoftDeletable {
    @Id
    @Column(updatable = false, nullable = false)
    @NonNull
    @Type(type = "uuid-char")
    private UUID uniqueId;

    @NonNull
    private String name;

    @Setter
    private transient SidedPlayer sidedPlayer;

    @OneToMany(mappedBy = "owner", targetEntity = CloudAccount.class, cascade = CascadeType.ALL)
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(targetEntity = CloudPermission.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "permissible")
    @Where(clause = "permissible_type='player'")
    private List<Permission> permissions = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdAt = Calendar.getInstance();
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updatedAt = Calendar.getInstance();
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar deletedAt;

    @Setter
    private transient long lastRefreshed;

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
