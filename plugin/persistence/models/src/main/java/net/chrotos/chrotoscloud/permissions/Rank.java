package net.chrotos.chrotoscloud.permissions;

import lombok.NonNull;
import net.chrotos.chrotoscloud.Model;
import net.chrotos.chrotoscloud.player.Player;

import java.util.Set;

public interface Rank extends Permissible, Model {
    Rank getParent();
    void setParent(Rank rank);
    @NonNull
    String getPrefix();
    boolean isTeam();
    @NonNull
    Set<Player> getPlayers();
    boolean isDefaultRank();
}
