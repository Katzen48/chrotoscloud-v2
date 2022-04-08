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
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;
import net.kyori.adventure.text.Component;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.*;

@Entity(name = "players")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@DynamicUpdate
@SQLDelete(sql = "UPDATE players SET deleted_at=now() WHERE unique_id = ?")
@Where(clause = "deleted_at IS NUlL")
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
    @Filter(name = "accountType")
    @Filter(name = "uniqueId")
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
    @Filter(name = "gameMode")
    @NonNull
    private Set<GameStatistic> stats = new HashSet<>();

    @OneToMany(mappedBy = "player", targetEntity = CloudGameState.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @Filter(name = "gameMode")
    @NonNull
    private Set<GameState> states = new HashSet<>();

    @OneToMany(mappedBy = "player", targetEntity = CloudPlayerInventory.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @Filter(name = "gameMode")
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
    public Collection<Account> getAccounts(AccountType type) {
        return Cloud.getInstance().getPersistence()
                .executeFiltered("accountType", Collections.singletonMap("type", type),
                        () -> getAccounts().stream().toList());
    }

    @Override
    public Account getAccount(UUID uniqueId) {
        return Cloud.getInstance().getPersistence()
                .executeFiltered("uniqueId", Collections.singletonMap("uniqueid", uniqueId),
                        () -> getAccounts().stream().findFirst().orElse(null));
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
    public Collection<? extends GameStatistic> getStats(@NonNull String gameMode) {
        return Cloud.getInstance().getPersistence()
                .executeFiltered("gameMode",
                        Collections.singletonMap("gameMode", gameMode), () -> getStats().stream().toList());
    }

    @Override
    @NonNull
    public Collection<? extends GameState> getStates(@NonNull String gameMode) {
        return Cloud.getInstance().getPersistence()
                .executeFiltered("gameMode",
                        Collections.singletonMap("gameMode", gameMode), () -> getStates().stream().toList());
    }

    @Override
    public PlayerInventory getInventory(@NonNull String gameMode) {
        return Cloud.getInstance().getPersistence()
                .executeFiltered("gameMode",
                        Collections.singletonMap("gameMode", gameMode),
                        () -> getInventories().stream().findFirst().orElse(null));
    }
}
