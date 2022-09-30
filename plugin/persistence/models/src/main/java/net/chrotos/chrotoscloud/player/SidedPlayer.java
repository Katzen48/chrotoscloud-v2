package net.chrotos.chrotoscloud.player;

import lombok.NonNull;
import net.kyori.adventure.text.TextComponent;

import java.net.InetAddress;
import java.util.Locale;
import java.util.UUID;

public interface SidedPlayer extends Kickable {
    Object getSidedObject();
    UUID getUniqueId();
    String getName();
    Locale getLocale();
    void setResourcePack(@NonNull String url, @NonNull String hash);
    void setResourcePack(@NonNull String url, @NonNull String hash, boolean required);
    void setResourcePack(@NonNull String url, @NonNull String hash, boolean required, TextComponent prompt);
    InetAddress getIPAddress();
}
