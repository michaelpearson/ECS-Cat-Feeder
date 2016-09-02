package catfeeder;

import catfeeder.feeder.CatfeederSocketApplication;
import com.j256.ormlite.logger.LocalLog;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Bootstrap {
    public static void main(String[] argv) throws IOException, InterruptedException {

        //System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");

        String port = System.getProperty("port");
        if(port == null) {
            port = "8080";
        }

        ResourceConfig resourceConfiguration = new ResourceConfig().packages("catfeeder.api");
        resourceConfiguration.register(JacksonFeature.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:" + port + "/api/"), resourceConfiguration, false);

        WebSocketEngine.getEngine().register("", "/ws", CatfeederSocketApplication.getInstance());
        server.getListener("grizzly").registerAddOn(new WebSocketAddOn());



        String pathPrefix = System.getProperty("prefix");
        if(pathPrefix == null) {
            pathPrefix = "";
        }
        StaticHttpHandler staticHandler = new StaticHttpHandler(pathPrefix + "web/");
        staticHandler.setFileCacheEnabled(false);
        server.getServerConfiguration().addHttpHandler(staticHandler);

        server.start();
        //Block forever
        Thread.currentThread().join();
    }
}
