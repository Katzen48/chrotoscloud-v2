package net.chrotos.chrotoscloud.games.states;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import net.chrotos.chrotoscloud.player.Player;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.Calendar;
import java.util.UUID;

@Entity(name = "game_states")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@DynamicUpdate
@SQLDelete(sql = "UPDATE game_states SET deleted_at=now() WHERE unique_id = ?")
@Where(clause = "deleted_at IS NUlL")
@SelectBeforeUpdate
@Filter(name = "gameMode")
public class CloudGameState implements GameState, SoftDeletable {
    @Id
    @Column(updatable = false, nullable = false)
    @NonNull
    @Type(type = "uuid-char")
    private UUID uniqueId;

    @NonNull
    @Column(updatable = false, nullable = false)
    private String name;

    @NonNull
    @Column(updatable = false, nullable = false)
    private String gameMode;

    @ManyToOne(targetEntity = CloudPlayer.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "player_unique_id", updatable = false)
    @NonNull
    private Player player;

    @NonNull
    @Type(type = "text")
    private String state;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdAt = Calendar.getInstance();
    @Temporal(TemporalType.TIMESTAMP)
    @Version
    private Calendar updatedAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar deletedAt;
}
