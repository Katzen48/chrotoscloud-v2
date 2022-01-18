package net.chrotos.chrotoscloud.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityExistsException extends RuntimeException {
    private final Object object;
}
