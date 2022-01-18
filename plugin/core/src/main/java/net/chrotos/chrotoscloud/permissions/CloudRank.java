package net.chrotos.chrotoscloud.permissions;

import lombok.*;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import net.chrotos.chrotoscloud.player.Player;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.*;

@Entity(name = "ranks")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@DynamicUpdate
@SQLDelete(sql = "UPDATE ranks SET deleted_at=now() WHERE unique_id = ?")
@Where(clause = "deleted_at=NUlL")
public class CloudRank extends CloudPermissible implements Rank, SoftDeletable {
    @Id
    @Column(updatable = false, nullable = false)
    @NonNull
    @Type(type = "uuid-char")
    private UUID uniqueId;

    @NonNull
    private String name;

    @OneToMany(targetEntity = CloudPermission.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "permissible_unique_id")
    @Where(clause = "permissible_type='rank'")
    private List<Permission> permissions = new ArrayList<>();

    @OneToMany(targetEntity = CloudPlayer.class, cascade = CascadeType.ALL, mappedBy = "rank")
    private List<Player> players = new ArrayList<>();

    private boolean team = false;

    @NonNull
    private String prefix;

    @Setter
    @OneToOne(cascade = CascadeType.ALL, targetEntity = CloudRank.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_unique_id")
    private Rank parent;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdAt = Calendar.getInstance();
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updatedAt = Calendar.getInstance();
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar deletedAt;

    @Override
    @Transactional
    @NonNull
    public Optional<Permission> getPermissionExact(@NonNull String permission) {
        Optional<Permission> optionalPermission = super.getPermissionExact(permission);

        return optionalPermission.isPresent() || getParent() == null ? optionalPermission : getParent().getPermissionExact(permission);
    }
}
