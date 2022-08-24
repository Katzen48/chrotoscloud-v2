package net.chrotos.chrotoscloud.rest.middleware;

import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import net.chrotos.chrotoscloud.rest.middleware.authentication.JWTAuthenticator;
import net.chrotos.chrotoscloud.rest.middleware.authentication.StaticTokenAuthenticator;

public class AuthenticationMiddleware implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        String authenticator = System.getenv("REST_AUTHENTICATION");
        if (authenticator == null || authenticator.isBlank()) {
            return;
        }

        switch (authenticator.toLowerCase()) {
            case "token":
                context.register(new StaticTokenAuthenticator());
                break;
            case "jwt":
                context.register(new JWTAuthenticator());
                break;
        }
    }
}
