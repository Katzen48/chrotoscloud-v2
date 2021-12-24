package net.chrotos.chrotoscloud.paper;

import com.google.auto.service.AutoService;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.CoreCloud;

@AutoService(Cloud.class)
public class PaperCloud extends CoreCloud {
    protected PaperCloud() {
        setCloudConfig(new PaperConfig());
    }
}
