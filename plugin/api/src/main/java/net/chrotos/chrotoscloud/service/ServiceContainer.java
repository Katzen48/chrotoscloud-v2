package net.chrotos.chrotoscloud.service;

import com.google.inject.Injector;
import lombok.NonNull;

public interface ServiceContainer {
    @NonNull
    Injector getServiceInjector();
}
