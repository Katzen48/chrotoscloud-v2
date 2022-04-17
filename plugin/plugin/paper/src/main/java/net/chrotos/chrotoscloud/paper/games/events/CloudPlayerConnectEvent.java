package net.chrotos.chrotoscloud.paper.games.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.games.GameServer;
import net.chrotos.chrotoscloud.player.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class CloudPlayerConnectEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final GameServer from;
    private final Player player;

    public boolean isNewConnection() {
        return from == null;
    }

    public boolean isSwitch() {
        return !isNewConnection();
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
