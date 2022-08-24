package net.chrotos.chrotoscloud.rest.middleware.authentication;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import lombok.NonNull;
import net.chrotos.chrotoscloud.rest.exception.UnauthorizedException;

import java.io.IOException;

public abstract class TokenAuthenticator implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String tokenString = requestContext.getHeaderString("Authorization");
        if (tokenString == null || tokenString.isEmpty()) {
            throw new UnauthorizedException();
        }

        if (!isTokenValid(tokenString)) {
            throw new UnauthorizedException();
        }
    }

    abstract boolean isTokenValid(@NonNull String token);
}
