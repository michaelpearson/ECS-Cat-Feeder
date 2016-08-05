package catfeeder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Bootstrap {
    public static void main(String[] argv) throws IOException {
        ResourceConfig rc = new ResourceConfig().packages("catfeeder.api");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:8080"), rc);
        server.start();
        System.in.read();
        server.shutdown();
    }
}
