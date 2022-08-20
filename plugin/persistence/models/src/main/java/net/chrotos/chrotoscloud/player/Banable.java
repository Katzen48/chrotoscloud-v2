package net.chrotos.chrotoscloud.player;

import lombok.NonNull;

import java.util.Calendar;
import java.util.Set;

public interface Banable {
    @NonNull
    Set<Ban> getBans();
    Ban getActiveBan();
    boolean isBanned();
    Ban ban(@NonNull String reason);
    Ban ban(@NonNull String reason, Calendar expiresAt);
    boolean unban();
}
