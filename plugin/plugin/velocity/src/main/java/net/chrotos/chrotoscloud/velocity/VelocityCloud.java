package net.chrotos.chrotoscloud.velocity;

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
    @Setter
    private CloudPlugin plugin;
    private final VelocityScheduler scheduler;

    public VelocityCloud() {
        this.scheduler = getServiceInjector().getInstance(VelocityScheduler.class);
        setCloudConfig(new VelocityConfig());
    }

    @Override
    public void initialize() {
        super.initialize();

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
