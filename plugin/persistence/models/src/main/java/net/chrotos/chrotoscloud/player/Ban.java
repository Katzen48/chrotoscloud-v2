package net.chrotos.chrotoscloud.player;

import lombok.NonNull;
import net.chrotos.chrotoscloud.Model;
import net.kyori.adventure.text.Component;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public interface Ban extends Model {
    UUID getUniqueId();
    Player getPlayer();
    @NonNull
    String getReason();
    boolean isActive();
    Calendar getCreatedAt();
    Calendar getExpiresAt();
    Component getBanMessage(@NonNull Locale locale);
    Component getBanMessage(@NonNull Locale locale, @NonNull TimeZone timeZone);
}
