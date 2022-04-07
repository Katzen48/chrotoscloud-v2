package net.chrotos.chrotoscloud.player;

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
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SelectBeforeUpdate
@FilterDef(name = "inventoryGamemode", parameters = {
        @ParamDef(name = "game_mode", type = "java.lang.String")})
public class CloudPlayerInventory implements PlayerInventory {
    @Id
    @Column(updatable = false, nullable = false)
    @NonNull
    @Type(type = "uuid-char")
    private UUID uniqueId;

    @NonNull
    @Column(updatable = false, nullable = false)
    private String gameMode;

    @ManyToOne(targetEntity = CloudPlayer.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "player_unique_id", updatable = false)
    @NonNull
    private Player player;

    @Setter
    @NonNull
    @Type(type = "text")
    private String content;

    @Setter(AccessLevel.NONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdAt = Calendar.getInstance();
    @Setter(AccessLevel.NONE)
    @Temporal(TemporalType.TIMESTAMP)
    @Version
    private Calendar updatedAt;
}
