package net.chrotos.chrotoscloud.player;

import lombok.NonNull;
import net.chrotos.chrotoscloud.Model;
import net.chrotos.chrotoscloud.economy.AccountHolder;
import net.chrotos.chrotoscloud.games.states.StateHolder;
import net.chrotos.chrotoscloud.games.stats.StatsHolder;
import net.chrotos.chrotoscloud.permissions.Permissible;
import net.chrotos.chrotoscloud.permissions.Rank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public interface Player extends AccountHolder, Banable, InventoryHolder, Kickable, Permissible, StatsHolder, StateHolder, Model {
    SidedPlayer getSidedPlayer();
    @NonNull
    String getName();
    @NonNull
    UUID getUniqueId();
    Rank getRank();
    void setRank(Rank rank);
    @NonNull
    Component getPrefixes();
    void setResourcePack(@NonNull String url, @NonNull String hash);
    void setResourcePack(@NonNull String url, @NonNull String hash, boolean required);
    void setResourcePack(@NonNull String url, @NonNull String hash, boolean required, @NonNull TextComponent prompt);
    TimeZone getTimeZone();
    TimeZone getTimeZone(Locale locale);
    Locale getLocale();
}
