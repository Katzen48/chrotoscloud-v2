package net.chrotos.chrotoscloud.service;

import com.google.inject.Injector;
import lombok.NonNull;
import net.chrotos.chrotoscloud.CloudConfig;

public interface ServiceContainer {
    @NonNull
    Injector getServiceInjector();

    @NonNull
    CloudConfig getCloudConfig();
}
