package net.chrotos.chrotoscloud.player;

import lombok.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private Player player;

    @NonNull
    private String reason;

    @CreationTimestamp
    private Calendar createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar expiresAt;

    @Override
    public boolean isActive() {
        return getExpiresAt() == null || getExpiresAt().before(Calendar.getInstance());
    }

    @Override
    public Component getBanMessage(@NonNull Locale locale) {
        Component message = Component.text("You have been banned for ", NamedTextColor.RED); // TODO translate
        message = message.append(Component.text(getReason(), NamedTextColor.GOLD));

        if (getExpiresAt() != null) {
            Calendar expiration = getExpiresAt();

            DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
            dateFormat.setTimeZone(convertLocaleToTimeZone(locale));

            message = message.append(Component.text(" until ", NamedTextColor.RED)); // TODO translate
            message = message.append(Component.text(dateFormat.format(expiration.getTime()), NamedTextColor.GOLD));
        }
        message = message.append(Component.text("!", NamedTextColor.RED));

        return message;
    }

    private TimeZone convertLocaleToTimeZone(@NonNull Locale locale) {
        if (Locale.GERMAN.equals(locale) || Locale.GERMANY.equals(locale)) {
            return TimeZone.getTimeZone("Europe/Berlin");
        } else if (Locale.UK.equals(locale)) {
            return TimeZone.getTimeZone("Europe/London");
        } else if (Locale.US.equals(locale)) {
            return TimeZone.getTimeZone("America/New_York");
        } else if (Locale.CANADA.equals(locale)) {
            return TimeZone.getTimeZone("America/Toronto");
        } else if (Locale.CHINA.equals(locale) || Locale.CHINESE.equals(locale) || Locale.SIMPLIFIED_CHINESE.equals(locale) || Locale.TRADITIONAL_CHINESE.equals(locale)) {
            return TimeZone.getTimeZone("Asia/Shanghai");
        } else {
            return TimeZone.getDefault();
        }
    }
}
