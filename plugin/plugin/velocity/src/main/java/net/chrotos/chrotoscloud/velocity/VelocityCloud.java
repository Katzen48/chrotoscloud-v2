package net.chrotos.chrotoscloud.velocity;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.chrotos.chrotoscloud.CoreCloud;
import net.chrotos.chrotoscloud.velocity.games.VelocityGameManager;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Getter
public class VelocityCloud extends CoreCloud {
    @Setter
    private ProxyServer proxyServer;
    @Setter
    private Logger logger;
    @Setter
    private Path dataDir;
    private final VelocityGameManager gameManager;
    private final Injector serviceInjector;
    @Setter
    private CloudPlugin plugin;
    private final VelocityScheduler scheduler;

    public VelocityCloud() {
        this.serviceInjector = Guice.createInjector(new VelocityModule(this));
        this.scheduler = getServiceInjector().getInstance(VelocityScheduler.class);
        setCloudConfig(new VelocityConfig());
        gameManager = getServiceInjector().getInstance(VelocityGameManager.class);
    }

    @Override
    public void initialize() {
        super.initialize();

        try {
            gameManager.initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        scheduler.initialize();
    }

    @Override
    @NonNull
    public String getHostname() {
        return System.getenv("HOSTNAME");
    }

    @Override
    public File getTranslationDir() {
        return new File(dataDir.toFile(), "translations");
    }
}
