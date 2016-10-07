package catfeeder;

import catfeeder.db.DatabaseClient;
import catfeeder.feeder.CatfeederSocketApplication;
import catfeeder.model.NotificationRegistrations;
import catfeeder.util.SendNotificationPush;
import com.j256.ormlite.logger.LocalLog;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bootstrap {


    public static void main(String[] argv) throws IOException, InterruptedException, URISyntaxException {
        setupLogging();

        //Get the port for heroku or default to port 8080
        int port;
        String portProperty = System.getProperty("port");
        if(portProperty == null) {
            port = 8080;
        } else {
            port = Integer.valueOf(portProperty);
        }

        //Create a resource configuration for Grizzly & create the server
        ResourceConfig resourceConfiguration = new ResourceConfig().packages("catfeeder.api");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(new URI(String.format("http://0.0.0.0:%d/api", port)), resourceConfiguration, false);

        //Create websocket server for feeders to connect to
        WebSocketEngine.getEngine().register("", "/ws", CatfeederSocketApplication.getInstance());
        server.getListener("grizzly").registerAddOn(new WebSocketAddOn());

        //Heroku setup needs a prefix to web asset files because it starts in a different working directory
        String pathPrefix = System.getProperty("prefix");
        if(pathPrefix == null) {
            pathPrefix = "";
        }

        //Setup the static file handler to server the frontend
        StaticHttpHandler staticHandler = new StaticHttpHandler(pathPrefix + "web/");
        staticHandler.setFileCacheEnabled(false);
        server.getServerConfiguration().addHttpHandler(staticHandler);

        server.start();

        //Block forever
        Thread.currentThread().join();
    }

    private static void setupLogging() {
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
        Logger l = Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler");
        l.setLevel(Level.FINEST);
        l.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        l.addHandler(ch);
    }
}
