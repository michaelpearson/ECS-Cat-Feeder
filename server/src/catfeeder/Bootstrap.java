package catfeeder;

import catfeeder.feeder.SocketManager;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bootstrap {
    public static void main(String[] argv) throws IOException, InterruptedException {
        setupLogging();
        ResourceConfig rc = new ResourceConfig().packages("catfeeder.api");
        rc.register(JacksonFeature.class);
        String port = System.getProperty("port");
        if(port == null) {
            port = "8080";
        }
        String pathPrefix = System.getProperty("prefix");
        if(pathPrefix == null) {
            pathPrefix = "";
        }

        SocketManager.init();

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:" + port + "/api/"), rc);
        StaticHttpHandler staticHandler = new StaticHttpHandler(pathPrefix + "web/");
        staticHandler.setFileCacheEnabled(false);
        server.getServerConfiguration().addHttpHandler(staticHandler);
        server.start();
        Thread.currentThread().join();
        server.shutdown();
    }

    private static void setupLogging() {
        Logger l = Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler");
        l.setLevel(Level.FINEST);
        l.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        l.addHandler(ch);
    }
}
