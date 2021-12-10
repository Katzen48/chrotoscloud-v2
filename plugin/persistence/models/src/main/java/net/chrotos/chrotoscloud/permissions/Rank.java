package net.chrotos.chrotoscloud.permissions;

import net.chrotos.chrotoscloud.player.Player;

import java.util.Collection;

public interface Rank extends Permissible {
    String getName();
    Rank getParent();
    String getPrefix();
    boolean isTeam();
    Collection<Player> getPlayers();
}
