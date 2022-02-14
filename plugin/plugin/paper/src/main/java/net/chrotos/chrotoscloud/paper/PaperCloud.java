package net.chrotos.chrotoscloud.paper;

import net.chrotos.chrotoscloud.CoreCloud;

public class PaperCloud extends CoreCloud {
    public PaperCloud() {
        setCloudConfig(new PaperConfig());
    }

    @Override
    public String getHostname() {
        return System.getenv("HOSTNAME");
    }
}
