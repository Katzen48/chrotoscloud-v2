package net.chrotos.chrotoscloud.player;

import net.kyori.adventure.text.Component;

public interface Kickable {
    default void kick() {
        kick(null);
    }
    void kick(Component message);
}
