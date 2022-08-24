package net.chrotos.chrotoscloud.rest.middleware;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class CacheHeader implements ContainerResponseFilter {
    private final Cache cache;
    private final String cacheType;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add("Cache-Control", String.format("%s, max-age=%d", cacheType, cache.seconds()));
    }
}
