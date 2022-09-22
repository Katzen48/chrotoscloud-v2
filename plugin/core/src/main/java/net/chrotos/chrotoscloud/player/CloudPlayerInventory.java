package net.chrotos.chrotoscloud.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.Calendar;
import java.util.UUID;

@Entity(name = "player_inventories")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@DynamicUpdate
@SQLDelete(sql = "UPDATE players SET deleted_at=now() WHERE unique_id = ? AND updated_at = ?")
@Where(clause = "deleted_at IS NUlL")
@SelectBeforeUpdate
@Filter(name = "gameMode")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CloudPlayerInventory implements PlayerInventory {
    @Id
    @Column(updatable = false, nullable = false)
    @NonNull
    @Type(type = "uuid-char")
    private UUID uniqueId;

    @NonNull
    @Column(updatable = false, nullable = false)
    private String gameMode;

    @ManyToOne(targetEntity = CloudPlayer.class)
    @JoinColumn(name = "player_unique_id", updatable = false)
    @NonNull
    @JsonIgnore
    private Player player;

    @Setter
    @NonNull
    @Type(type = "text")
    private String content;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    private Calendar createdAt;
    @Setter(AccessLevel.NONE)
    @Temporal(TemporalType.TIMESTAMP)
    @Version
    private Calendar updatedAt;
}
