package net.chrotos.chrotoscloud;

import lombok.Getter;
import net.chrotos.chrotoscloud.persistence.PersistenceAdapter;

import java.util.Iterator;
import java.util.ServiceLoader;

@Getter
public abstract class Cloud {
    private static Cloud instance;
    protected PersistenceAdapter persistence;
    protected CloudConfig cloudConfig;

    public static Cloud getInstance() {
        if (Cloud.instance == null) {
            ServiceLoader<Cloud> serviceLoader = ServiceLoader.load(Cloud.class);

            Iterator<Cloud> iterator = serviceLoader.iterator();

            if (!iterator.hasNext()) {
                throw new IllegalArgumentException("No Implementation found!");
            }

            instance = iterator.next();
        }

        return instance;
    }

    // Management stuff (loading, initialization)

    /**
     * Load the module with it's services, before it can be initialized
     * @throws IllegalStateException if the a service is missing it's implementing module
     */
    public abstract void load();

    /**
     * Initialize the module, after it has been loaded
     * @throws IllegalStateException if the module has not been loaded
     */
    public abstract void initialize();

    public abstract boolean isInitialized();

    public abstract boolean isLoaded();
}
