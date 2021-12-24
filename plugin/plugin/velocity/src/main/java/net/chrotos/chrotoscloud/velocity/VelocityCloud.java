package net.chrotos.chrotoscloud.velocity;

import com.google.auto.service.AutoService;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.CoreCloud;

@AutoService(Cloud.class)
public class VelocityCloud extends CoreCloud {
    public VelocityCloud() {
        setCloudConfig(new VelocityConfig());
    }
}
