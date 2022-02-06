package net.chrotos.chrotoscloud.chat;

import net.chrotos.chrotoscloud.player.Player;

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
    String getPrefix(Player player); //TODO Change to kyori component
    boolean isActive(Player player);
    String getColor(); //TODO Change to kyori component
}
