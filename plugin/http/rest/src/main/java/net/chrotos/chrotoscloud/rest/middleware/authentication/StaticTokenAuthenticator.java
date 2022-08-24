package net.chrotos.chrotoscloud.rest.middleware.authentication;

import lombok.NonNull;

public class StaticTokenAuthenticator extends TokenAuthenticator {
    @Override
    boolean isTokenValid(@NonNull String token) {
        return token.equals("Bearer " + System.getenv("REST_AUTHENTICATION_TOKEN"));
    }
}
