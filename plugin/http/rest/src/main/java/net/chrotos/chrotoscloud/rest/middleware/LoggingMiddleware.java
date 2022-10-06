package net.chrotos.chrotoscloud.rest.middleware;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingMiddleware implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler").log(Level.FINE,
                "Request to {0} {1}", new Object[]{requestContext.getMethod(), requestContext.getUriInfo().getRequestUri()});
    }
}
