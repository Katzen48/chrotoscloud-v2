package net.chrotos.chrotoscloud.chat;

import lombok.NonNull;
import net.chrotos.chrotoscloud.player.Player;
import net.kyori.adventure.text.Component;

public interface ChatManager {
    @NonNull
    Component getPrefixes(@NonNull Player player);
    void registerPrefix(@NonNull ChatPrefix prefix);
    @NonNull
    Component getRankColored(@NonNull Player player);
}