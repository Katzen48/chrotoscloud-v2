package net.chrotos.chrotoscloud.rest.middleware;

import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import lombok.Getter;
import net.chrotos.chrotoscloud.rest.middleware.authentication.JWTAuthenticator;
import net.chrotos.chrotoscloud.rest.middleware.authentication.StaticTokenAuthenticator;

public class AuthenticationMiddleware implements DynamicFeature {
    @Getter
    private static boolean authenticationRequired = false;

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        String authenticator = System.getenv("REST_AUTHENTICATION");
        if (authenticator == null || authenticator.isBlank()) {
            return;
        }

        switch (authenticator.toLowerCase()) {
            case "token":
                context.register(new StaticTokenAuthenticator());
                authenticationRequired = true;
                break;
            case "jwt":
                context.register(new JWTAuthenticator());
                authenticationRequired = true;
                break;
        }
    }
}
