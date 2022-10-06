package net.chrotos.chrotoscloud.rest.middleware.authentication;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import lombok.NonNull;
import net.chrotos.chrotoscloud.rest.exception.UnauthorizedException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TokenAuthenticator implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String tokenString = requestContext.getHeaderString("Authorization");
        if (tokenString == null || tokenString.isEmpty()) {
            throw new UnauthorizedException();
        }

        Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler").log(Level.FINE,
                "Authenticating using {0}", getClass().getName());

        if (!isTokenValid(tokenString)) {
            throw new UnauthorizedException();
        }
    }

    abstract boolean isTokenValid(@NonNull String token);
}
