package net.chrotos.chrotoscloud.rest;

import net.chrotos.chrotoscloud.Cloud;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.net.URI;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestServer {
    public static final String BASE_URI = "http://0.0.0.0:8080";
    private final HttpServer httpServer;

    public RestServer() {
        httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), new RestApplication(), false);
        ServiceLocatorUtilities.createAndPopulateServiceLocator();
    }

    public void start() {
        Cloud.setServiceClassLoader(getClass().getClassLoader());
        Cloud cloud = Cloud.getInstance();

        cloud.load();
        cloud.initialize();

        try {
            Logger l = Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler");
            l.setLevel(Level.FINE);
            l.setUseParentHandlers(false);
            ConsoleHandler ch = new ConsoleHandler();
            ch.setLevel(Level.ALL);
            l.addHandler(ch);

            httpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
