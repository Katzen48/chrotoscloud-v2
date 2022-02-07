package net.chrotos.chrotoscloud.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountType {
    BANK(true), HAND(false);

    private final boolean shareable;
}
