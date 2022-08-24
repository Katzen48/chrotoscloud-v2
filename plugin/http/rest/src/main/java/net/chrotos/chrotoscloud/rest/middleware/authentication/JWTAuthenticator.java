package net.chrotos.chrotoscloud.rest.middleware.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.NonNull;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class JWTAuthenticator extends TokenAuthenticator {
    private KeyPair keyPairCache = null;

    @Override
    boolean isTokenValid(@NonNull String token) {
        if (!token.startsWith("Bearer ")) {
            return false;
        }
        String jwt = token.substring(7);
        if (jwt.length() == 0) {
            return false;
        }

        String algorithm = System.getenv("REST_AUTHENTICATION_ALGORITHM");
        if (algorithm == null) {
            return false;
        }

        return switch (algorithm.toLowerCase()) {
            case "hmac" -> verifyHmacToken(jwt);
            case "rsa" -> verifyRsaToken(jwt);
            default -> false;
        };
    }

    private boolean verifyHmacToken(@NonNull String token) {
        String secret = System.getenv("REST_AUTHENTICATION_SECRET");
        if (secret == null || secret.isEmpty()) {
            return false;
        }

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return isJwtValid(algorithm, token);
    }

    private boolean verifyRsaToken(@NonNull String token) {
        KeyPair keyPair = getKeyPair();
        if (keyPair == null) {
            return false;
        }

        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
        return isJwtValid(algorithm, token);
    }

    private boolean isJwtValid(@NonNull Algorithm algorithm, @NonNull String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .acceptLeeway(5L)
                    .build();
            jwtVerifier.verify(token);

            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private KeyPair getKeyPair() {
        if (keyPairCache != null) {
            return keyPairCache;
        }

        String pubkey = System.getenv("REST_AUTHENTICATION_PUBKEY");
        String privkey = System.getenv("REST_AUTHENTICATION_PRIVKEY");
        if (pubkey == null || privkey == null || pubkey.isBlank() || privkey.isBlank()) {
            return null;
        }

        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");

            byte[] pubkeyContent = new PemReader(new StringReader(pubkey)).readPemObject().getContent();
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubkeyContent);

            byte[] prikeyContent = new PemReader(new StringReader(privkey)).readPemObject().getContent();
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(prikeyContent);

            return (keyPairCache = new KeyPair(factory.generatePublic(pubKeySpec), factory.generatePrivate(privKeySpec)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static {
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
}
