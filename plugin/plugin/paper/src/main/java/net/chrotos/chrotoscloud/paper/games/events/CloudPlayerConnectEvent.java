package net.chrotos.chrotoscloud.paper.games.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.games.GameServer;
import net.chrotos.chrotoscloud.player.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class CloudPlayerConnectEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final GameServer from;
    @NonNull
    private final Player player;

    public CloudPlayerConnectEvent(GameServer from, @NonNull Player player) {
        super(true);
        this.from = from;
        this.player = player;
    }

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
