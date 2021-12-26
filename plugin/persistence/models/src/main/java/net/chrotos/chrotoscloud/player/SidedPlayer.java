package net.chrotos.chrotoscloud.player;

import java.util.Locale;
import java.util.UUID;

public interface SidedPlayer {
    Object getSidedObject();
    UUID getUniqueId();
    String getName();
    Locale getLocale();
}
