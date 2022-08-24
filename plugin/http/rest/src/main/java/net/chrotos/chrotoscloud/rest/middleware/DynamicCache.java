package net.chrotos.chrotoscloud.rest.middleware;

import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import net.chrotos.chrotoscloud.rest.middleware.authentication.Authenticate;

import java.lang.reflect.Method;

public class DynamicCache implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        Cache cache = getCache(resourceInfo.getResourceMethod());

        if (cache == null) {
            return;
        }

        context.register(new CacheHeader(cache, requiresAuthentication(resourceInfo.getResourceMethod()) ? "private" : "public"));
    }

    private Cache getCache(Method method) {
       return method.getDeclaredAnnotation(Cache.class);
    }

    private boolean requiresAuthentication(Method method) {
        return method.getDeclaredAnnotation(Authenticate.class) != null;
    }
}
