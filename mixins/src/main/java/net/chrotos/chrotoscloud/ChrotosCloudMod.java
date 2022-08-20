package net.chrotos.chrotoscloud;

import com.google.inject.Inject;
import space.vectrix.ignite.api.Platform;

import java.util.logging.Logger;

public class ChrotosCloudMod {
    private final Logger logger;
    private final Platform platform;

    @Inject
    public ChrotosCloudMod(final Logger logger, final Platform platform) {
        this.logger = logger;
        this.platform = platform;
    }
}
