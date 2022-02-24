package net.chrotos.chrotoscloud.velocity;

import net.chrotos.chrotoscloud.CoreCloud;

public class VelocityCloud extends CoreCloud {
    public VelocityCloud() {
        setCloudConfig(new VelocityConfig());
    }

    @Override
    public String getHostname() {
        return System.getenv("HOSTNAME");
    }
}
