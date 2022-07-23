package net.chrotos.chrotoscloud.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

import java.text.SimpleDateFormat;

@ApplicationPath("/")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        packages("net.chrotos.chrotoscloud.rest.services");

        register(getJacksonProvider());
        register(new CORSFilter());
    }

    private JacksonJsonProvider getJacksonProvider() {
        JacksonJsonProvider provider = new JacksonJsonProvider();
        provider.setMapper(new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));

        return provider;
    }
}
