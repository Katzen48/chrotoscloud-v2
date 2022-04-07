package net.chrotos.chrotoscloud.player;

import lombok.*;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.economy.Account;
import net.chrotos.chrotoscloud.economy.AccountType;
import net.chrotos.chrotoscloud.economy.CloudAccount;
import net.chrotos.chrotoscloud.games.states.CloudGameState;
import net.chrotos.chrotoscloud.games.states.GameState;
import net.chrotos.chrotoscloud.games.stats.CloudGameStatistic;
import net.chrotos.chrotoscloud.games.stats.GameStatistic;
import net.chrotos.chrotoscloud.permissions.*;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;
import net.kyori.adventure.text.Component;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.*;
import java.util.stream.Collectors;

@Entity(name = "players")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@DynamicUpdate
@SQLDelete(sql = "UPDATE players SET deleted_at=now() WHERE unique_id = ?")
@Where(clause = "deleted_at IS NUlL")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SelectBeforeUpdate
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

    @OneToMany(mappedBy = "owner", targetEntity = CloudAccount.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @NonNull
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(targetEntity = CloudPermission.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "permissible_unique_id")
    @Where(clause = "permissible_type='player'")
    @NonNull
    private Set<Permission> permissions = new HashSet<>();

    @Setter
    @ManyToOne(targetEntity = CloudRank.class)
    @JoinColumn(name = "rank_unique_id")
    private Rank rank;

    @OneToMany(mappedBy = "player", targetEntity = CloudGameStatistic.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @NonNull
    private Set<GameStatistic> stats = new HashSet<>();

    @OneToMany(mappedBy = "player", targetEntity = CloudGameState.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @NonNull
    private Set<GameState> states = new HashSet<>();

    @OneToMany(mappedBy = "player", targetEntity = CloudPlayerInventory.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @NonNull
    private Set<PlayerInventory> inventories = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdAt = Calendar.getInstance();
    @Temporal(TemporalType.TIMESTAMP)
    @Version
    private Calendar updatedAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar deletedAt = null;

    @Override
    public Set<Account> getAccounts(AccountType type) {
        return getAccounts().stream().filter(
                account -> account.getAccountType() == type
        ).collect(Collectors.toSet());
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

    @Override
    @NonNull
    public Component getPrefixes() {
        return Cloud.getInstance().getChatManager().getPrefixes(this);
    }

    @Override
    @NonNull
    public Set<GameStatistic> getStats(@NonNull String gameMode) {
        return getStats().stream().filter(
                gameStatistic -> gameStatistic.getGameMode().equals(gameMode)
        ).collect(Collectors.toSet());
    }

    @Override
    public @NonNull Set<GameState> getStates(@NonNull String gameMode) {
        return getStates().stream().filter(
                gameState -> gameState.getGameMode().equals(gameMode)
        ).collect(Collectors.toSet());
    }

    @Override
    public PlayerInventory getInventory(@NonNull String gameMode) {
        return getInventories().stream().filter(
                playerInventory -> playerInventory.getGameMode().equals(gameMode)
        ).findFirst().orElse(null);
    }
}
