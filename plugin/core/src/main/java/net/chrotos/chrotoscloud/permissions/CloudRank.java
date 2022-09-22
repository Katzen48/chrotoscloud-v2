package net.chrotos.chrotoscloud.permissions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import net.chrotos.chrotoscloud.player.Player;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.transaction.Transactional;
import java.util.*;

@Entity(name = "ranks")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@DynamicUpdate
@SQLDelete(sql = "UPDATE ranks SET deleted_at=now() WHERE unique_id = ? AND updated_at = ?")
@Immutable
@Where(clause = "deleted_at IS NUlL")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CloudRank extends CloudPermissible implements Rank, SoftDeletable {
    @Id
    @Column(updatable = false, nullable = false)
    @NonNull
    @Type(type = "uuid-char")
    private UUID uniqueId;

    @NonNull
    private String name;

    @OneToMany(targetEntity = CloudPermission.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "permissible_unique_id")
    @Where(clause = "permissible_type='rank'")
    @JsonIgnore
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Permission> permissions = new HashSet<>();

    @OneToMany(targetEntity = CloudPlayer.class, cascade = CascadeType.ALL, mappedBy = "rank")
    @JsonIgnore
    private Set<Player> players = new HashSet<>();

    private boolean team = false;

    @NonNull
    private String prefix;

    @Setter
    @OneToOne(cascade = CascadeType.ALL, targetEntity = CloudRank.class)
    @JoinColumn(name = "parent_unique_id")
    @JsonIgnore
    private Rank parent;

    @JsonIgnore
    private boolean defaultRank = false;

    @CreationTimestamp
    @JsonIgnore
    private Calendar createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    @Version
    @JsonIgnore
    private Calendar updatedAt;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Calendar deletedAt;

    @Override
    @Transactional
    @NonNull
    public Optional<Permission> getPermissionExact(@NonNull String permission) {
        Optional<Permission> optionalPermission = super.getPermissionExact(permission);

        return optionalPermission.isPresent() || getParent() == null ? optionalPermission : getParent().getPermissionExact(permission);
    }
}
