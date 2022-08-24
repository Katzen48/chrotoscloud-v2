package net.chrotos.chrotoscloud.rest.middleware;

import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import net.chrotos.chrotoscloud.rest.middleware.authentication.Authenticate;
import net.chrotos.chrotoscloud.rest.middleware.authentication.JWTAuthenticator;
import net.chrotos.chrotoscloud.rest.middleware.authentication.StaticTokenAuthenticator;
import net.chrotos.chrotoscloud.rest.middleware.authentication.TokenAuthenticator;

import java.lang.reflect.Method;

public class AuthenticationMiddleware implements DynamicFeature {
    private static TokenAuthenticator authenticatorCache;

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (!requiresAuthentication(resourceInfo.getResourceMethod())) {
            return;
        }

        TokenAuthenticator authenticator = getAuthenticator();
        if (authenticator == null) {
            return;
        }

        context.register(authenticator);
    }

    private TokenAuthenticator getAuthenticator() {
        if (authenticatorCache != null) {
            return authenticatorCache;
        }

        String authenticator = System.getenv("REST_AUTHENTICATION");
        if (authenticator == null || authenticator.isBlank()) {
            return null;
        }

        return switch (authenticator.toLowerCase()) {
            case "token" -> (authenticatorCache = new StaticTokenAuthenticator());
            case "jwt" -> (authenticatorCache = new JWTAuthenticator());
            default -> null;
        };
    }

    private boolean requiresAuthentication(Method method) {
        return method.getDeclaredAnnotation(Authenticate.class) != null;
    }
}
