package net.chrotos.chrotoscloud.paper;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.chrotos.chrotoscloud.player.SidedPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
    public void setResourcePack(@NonNull String url, @NonNull String hash) {
        setResourcePack(url, hash, false);
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash, boolean required) {
        setResourcePack(url, hash, required, null);
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash, boolean required, TextComponent prompt) {
        sidedObject.setResourcePack(url, hash, required, prompt != null && !prompt.content().equals("") ? prompt : null);
        sentResourcePackHash = hash;
    }

    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            resourcePackHash = sentResourcePackHash;
        }
    }
}
