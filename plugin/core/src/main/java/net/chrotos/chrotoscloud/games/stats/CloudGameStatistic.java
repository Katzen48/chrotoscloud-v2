package net.chrotos.chrotoscloud.games.stats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.*;
import net.chrotos.chrotoscloud.persistence.SoftDeletable;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import net.chrotos.chrotoscloud.player.Player;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.Calendar;
import java.util.UUID;

@Entity(name = "game_stats")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@DynamicUpdate
@SelectBeforeUpdate
@SQLDelete(sql = "UPDATE game_stats SET deleted_at=now() WHERE unique_id = ?")
@Where(clause = "deleted_at IS NUlL")
@Filter(name = "gameMode")
public class CloudGameStatistic implements GameStatistic, SoftDeletable {
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
    @JsonIncludeProperties({"unique_id", "name"})
    private Player player;

    @Setter
    @NonNull
    private double value;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createdAt = Calendar.getInstance();
    @Temporal(TemporalType.TIMESTAMP)
    @Version
    private Calendar updatedAt;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Calendar deletedAt;

    @Override
    public void increment() {
        increment(1);
    }

    @Override
    public void increment(double value) {
        setValue(getValue() + value);
    }

    @Override
    public void decrement() {
        decrement(1);
    }

    @Override
    public void decrement(double value) {
        setValue(getValue() - value);
    }
}
