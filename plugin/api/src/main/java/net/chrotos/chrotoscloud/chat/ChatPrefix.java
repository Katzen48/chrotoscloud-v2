package net.chrotos.chrotoscloud.chat;

import net.chrotos.chrotoscloud.player.Player;
import net.kyori.adventure.text.format.TextColor;

public interface ChatPrefix {
    /**
     * Slot 0 is Slot for Ranks. Can be overwritten.
     * @return the slot index
     */
    int getSlot();
    /**
     * Rank Prefix has Priority 10
     * @return the priority index
     */
    int getPriority();
    String getPrefix(Player player);
    boolean isActive(Player player);
    TextColor getColor();
}
