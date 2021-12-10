package net.chrotos.chrotoscloud.persistence;

import java.util.HashMap;

public class PersistenceRegistry {
    private static final HashMap<String, Class<? extends PersistenceAdapter>> MANAGERS = new HashMap<>();

    public static void registerManager(Class<? extends PersistenceAdapter> manager) {
        MANAGERS.put(manager.getName(), manager);
    }
}
