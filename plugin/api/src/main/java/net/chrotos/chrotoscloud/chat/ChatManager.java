package net.chrotos.chrotoscloud.chat;

import net.chrotos.chrotoscloud.player.Player;

public interface ChatManager {
    String getPrefixes(Player player); //TODO Change to kyori component
    void registerPrefix(ChatPrefix prefix);
    String getRankColor(Player player); //TODO Change to kyori component
}