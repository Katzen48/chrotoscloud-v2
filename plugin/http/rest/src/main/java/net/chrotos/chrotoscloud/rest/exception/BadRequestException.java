package net.chrotos.chrotoscloud.rest.exception;

public class BadRequestException extends JsonException {

    public BadRequestException(String message) {
        super(message == null ? "Bad Request" : message, 400);
    }

    public BadRequestException() {
        this(null);
    }
}
