package net.chrotos.chrotoscloud.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.chrotos.chrotoscloud.CoreCloud;
import net.chrotos.chrotoscloud.velocity.games.VelocityGameManager;
import org.slf4j.Logger;

import java.io.IOException;

@Getter
public class VelocityCloud extends CoreCloud {
    @Setter
    private ProxyServer proxyServer;
    @Setter
    private Logger logger;
    private final VelocityGameManager gameManager;

    public VelocityCloud() {
        setCloudConfig(new VelocityConfig());
        gameManager = new VelocityGameManager(this);
    }

    @Override
    public void initialize() {
        super.initialize();

        try {
            gameManager.initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NonNull
    public String getHostname() {
        return System.getenv("HOSTNAME");
    }
}
