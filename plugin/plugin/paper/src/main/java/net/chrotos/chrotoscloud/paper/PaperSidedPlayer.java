package net.chrotos.chrotoscloud.paper;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.chrotos.chrotoscloud.player.SidedPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.util.Locale;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PaperSidedPlayer implements SidedPlayer {
    private final Player sidedObject;
    private String resourcePackHash;
    @Setter
    private String sentResourcePackHash;

    @Override
    public UUID getUniqueId() {
        return getSidedObject().getUniqueId();
    }

    @Override
    public String getName() {
        return getSidedObject().getName();
    }

    @Override
    public Locale getLocale() {
        return getSidedObject().locale();
    }

    @Override
    public void setResourcePack(@NonNull String url) {
        sidedObject.setResourcePack(url);
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash) {
        sidedObject.setResourcePack(url, hash);
        sentResourcePackHash = hash;
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash, boolean required) {
        sidedObject.setResourcePack(url, hash, required);
        sentResourcePackHash = hash;
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash, boolean required, @NonNull Component prompt) {
        sidedObject.setResourcePack(url, hash, required, prompt);
        sentResourcePackHash = hash;
    }

    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            resourcePackHash = sentResourcePackHash;
        }
    }
}
