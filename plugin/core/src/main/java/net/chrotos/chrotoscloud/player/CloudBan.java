package net.chrotos.chrotoscloud.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

@Entity(name = "bans")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@SQLDelete(sql = "UPDATE bans SET expires_at=now() WHERE unique_id = ?")
@SelectBeforeUpdate
@FilterDef(name = "active", defaultCondition = "`expires_at` IS NULL OR `expires_at` >= now()")
@Filter(name = "active")
public class CloudBan implements Ban {
    @Id
    @Column(updatable = false, nullable = false)
    @NonNull
    @Type(type = "uuid-char")
    private UUID uniqueId;

    @ManyToOne(targetEntity = CloudPlayer.class)
    @JoinColumn(name = "player_unique_id", updatable = false)
    @NonNull
    @JsonIgnore
    private Player player;

    @NonNull
    private String reason;

    @CreationTimestamp
    private Calendar createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar expiresAt;

    @Override
    @JsonIgnore
    public boolean isActive() {
        return getExpiresAt() == null || getExpiresAt().after(Calendar.getInstance());
    }

    @Override
    public Component getBanMessage(@NonNull Locale locale) {
        return getBanMessage(locale, getPlayer().getTimeZone());
    }

    @Override
    public Component getBanMessage(@NonNull Locale locale, @NonNull TimeZone timeZone) {
        Component message = Component.text("You have been banned for ", NamedTextColor.RED); // TODO translate
        message = message.append(Component.text(getReason(), NamedTextColor.GOLD));

        if (getExpiresAt() != null) {
            Calendar expiration = getExpiresAt();
            LocalDateTime local = LocalDateTime.ofInstant(expiration.toInstant(), timeZone.toZoneId());
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale);

            message = message.append(Component.text(" until ", NamedTextColor.RED)); // TODO translate
            message = message.append(Component.text(formatter.format(local), NamedTextColor.GOLD));
        }
        message = message.append(Component.text("!", NamedTextColor.RED));

        return message;
    }
}
