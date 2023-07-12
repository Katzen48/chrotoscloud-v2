package net.chrotos.chrotoscloud;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.maxmind.geoip2.DatabaseReader;
import lombok.Getter;
import lombok.NonNull;
import net.chrotos.chrotoscloud.cache.RedisCacheAdapter;
import net.chrotos.chrotoscloud.chat.ChatManager;
import net.chrotos.chrotoscloud.chat.CoreChatManager;
import net.chrotos.chrotoscloud.jobs.CloudJobManager;
import net.chrotos.chrotoscloud.messaging.queue.RabbitQueueAdapter;
import net.chrotos.chrotoscloud.persistence.PersistenceAdapter;
import net.chrotos.chrotoscloud.player.CloudPlayerManager;
import net.chrotos.chrotoscloud.service.ServiceProvider;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;

import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class CoreCloud extends Cloud {
    private static boolean loaded;
    private static boolean initialized;
    @Getter
    private final DatabaseReader geoIp;
    @Getter
    private TranslationRegistry translationRegistry;
    @Getter
    private CloudJobManager jobManager;
    @Getter
    private final Injector serviceInjector;
    private final Iterable<ServiceProvider> serviceProviders;

    protected CoreCloud() {
        // TODO refactor to service provider
        DatabaseReader geoIp = null;
        try {
            geoIp = new DatabaseReader.Builder(new File("/usr/local/share/GeoIP/GeoLite2-City.mmdb")).build();
        } catch (FileNotFoundException e) {
            System.err.println("MaxMind GeoLite2-City database not installed. Cannot resolve location data." +
                    "Please install the database in /usr/local/share/GeoIP/GeoLite2-City.mmdb or use geoipupdate!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.geoIp = geoIp;

        serviceProviders = ServiceLoader.load(ServiceProvider.class);
        serviceInjector = Guice.createInjector(serviceProviders);
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    public void load() {
        if (loaded) {
            return;
        }

        // Load all service providers
        serviceProviders.forEach(serviceProvider -> serviceProvider.load(this));

        loaded = true;
    }

    public void initialize() {
        if (initialized) {
            return;
        }

        if (!loaded) {
            throw new IllegalArgumentException("Not loaded! Cannot initialize!");
        }

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        // Initialize all service providers
        serviceProviders.forEach(serviceProvider -> serviceProvider.initialize(this));

        RedisCacheAdapter redisAdapter = null;
        if (shouldLoadCache()) {
            redisAdapter = getServiceInjector().getInstance(RedisCacheAdapter.class);
            this.cache = redisAdapter;
            this.cache.configure(getCloudConfig());

            this.jobManager = getServiceInjector().getInstance(CloudJobManager.class);
            getJobManager().initialize();
        }

        if (shouldLoadPubSub()) {
            assert redisAdapter != null;
            this.pubSub = redisAdapter.getPubSub();
        }

        Thread.currentThread().setContextClassLoader(getServiceClassLoader());

        Thread.currentThread().setContextClassLoader(loader);

        initializeTranslations();

        initialized = true;

        // Boot all service providers
        serviceProviders.forEach(serviceProvider -> serviceProvider.boot(this));
    }

    private void initializeTranslations() {
        File translationsDir = getTranslationDir();
        if (translationsDir == null) {
            return;
        }

        if (!translationsDir.exists()) {
            translationsDir.mkdirs();
        }

        CodeSource src = getClass().getProtectionDomain().getCodeSource();
        if (src != null) {
            try {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry entry;
                while((entry = zip.getNextEntry()) != null) {
                    if (!entry.getName().startsWith("translations") || !entry.getName().contains("chrotoscloud")) {
                        continue;
                    }

                    File translationFile = new File(translationsDir, entry.getName().replace("translations/", ""));
                    if (!translationFile.exists()) {
                        translationFile.createNewFile();

                        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(translationFile));
                        byte[] buffer = new byte[1024];

                        int count;
                        while ((count = zip.read(buffer)) != -1) {
                            out.write(buffer, 0, count);
                        }

                        out.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        translationRegistry = TranslationRegistry.create(Key.key("chrotoscloud"));
        Arrays.stream(translationsDir.listFiles((dir, name) -> name.endsWith(".properties"))).forEach(file -> {
            try {
                String[] fileNameParts = file.getName().split("_", 3);
                Locale locale = fileNameParts.length > 2 ?
                        Locale.forLanguageTag(fileNameParts[2].replace(".properties", "")) : Locale.US;

                ResourceBundle resourceBundle = new PropertyResourceBundle(new FileInputStream(file));

                translationRegistry.registerAll(locale, resourceBundle, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        GlobalTranslator.translator().addSource(translationRegistry);
    }

    protected boolean shouldLoadPubSub() {
        return true;
    }

    protected boolean shouldLoadCache() {
        return true;
    }

    protected void setCloudConfig(CloudConfig cloudConfig) {
        this.cloudConfig = cloudConfig;
    }
}
