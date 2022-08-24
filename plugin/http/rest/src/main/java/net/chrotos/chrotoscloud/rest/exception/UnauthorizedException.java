package net.chrotos.chrotoscloud.rest.exception;

public class UnauthorizedException extends JsonException {
    public UnauthorizedException(String message) {
        super(message == null ? "Unauthorized" : message, 401);
    }

    public UnauthorizedException() {
        this(null);
    }
}
