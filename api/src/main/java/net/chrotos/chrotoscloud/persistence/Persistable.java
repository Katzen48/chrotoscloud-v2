package net.chrotos.chrotoscloud.persistence;

import java.util.Calendar;

public interface Persistable {
    Calendar getCreatedAt();
    Calendar getUpdatedAt();
}
