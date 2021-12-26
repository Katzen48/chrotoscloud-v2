package net.chrotos.chrotoscloud.paper;

import lombok.Getter;
import net.chrotos.chrotoscloud.player.SidedPlayer;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.UUID;

@Getter
public class PaperSidedPlayer implements SidedPlayer {
    private Player sidedObject;

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
}
