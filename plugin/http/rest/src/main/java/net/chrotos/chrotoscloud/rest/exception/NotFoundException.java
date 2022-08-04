package net.chrotos.chrotoscloud.rest.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class NotFoundException extends JsonException {

    public NotFoundException(String message) {
        super(message == null ? "Not found" : message, 404);
    }

    public NotFoundException() {
        this(null);
    }
}
