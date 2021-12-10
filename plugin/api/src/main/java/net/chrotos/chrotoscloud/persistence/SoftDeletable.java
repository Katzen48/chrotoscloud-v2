package net.chrotos.chrotoscloud.persistence;

import java.util.Calendar;

public interface SoftDeletable extends Persistable {
    Calendar getDeletedAt();
}
