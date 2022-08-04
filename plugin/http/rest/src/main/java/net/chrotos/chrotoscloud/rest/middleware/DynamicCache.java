package net.chrotos.chrotoscloud.rest.middleware;

import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;

import java.lang.reflect.Method;

public class DynamicCache implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        Cache cache = getCache(resourceInfo.getResourceMethod());

        if (cache == null) {
            return;
        }

        context.register(new CacheHeader(cache));
    }

    private Cache getCache(Method method) {
       return method.getDeclaredAnnotation(Cache.class);
    }
}
