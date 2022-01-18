package net.chrotos.chrotoscloud.player;

import lombok.*;
import net.chrotos.chrotoscloud.economy.Account;
import net.chrotos.chrotoscloud.economy.AccountType;
import net.chrotos.chrotoscloud.economy.CloudAccount;
import net.chrotos.chrotoscloud.permissions.*;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
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
@SQLDelete(sql = "UPDATE players SET deleted_at=now() WHERE unique_id = ?")
@Where(clause = "deleted_at=NUlL")
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
    @JoinColumn(name = "permissible_unique_id")
    @Where(clause = "permissible_type='player'")
    private List<Permission> permissions = new ArrayList<>();

    @Setter
    @ManyToOne(targetEntity = CloudRank.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_unique_id")
    private Rank rank;

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

    @Override
    @NonNull
    public Optional<Permission> getPermissionExact(@NonNull String permission) {
        Optional<Permission> optionalPermission = super.getPermissionExact(permission);

        return optionalPermission.isPresent() || getRank() == null ? optionalPermission : getRank().getPermissionExact(permission);
    }
}
