package net.chrotos.chrotoscloud.paper;

import net.chrotos.chrotoscloud.CoreCloud;

public class PaperCloud extends CoreCloud {
    protected PaperCloud(CloudPlugin plugin) {
        setCloudConfig(new PaperConfig());
    }
}
