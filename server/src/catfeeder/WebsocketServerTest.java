package catfeeder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketEngine;

import java.io.IOException;

public class WebsocketServerTest {

    public static void main(String[] argv) throws IOException, InterruptedException {
        HttpServer server = HttpServer.createSimpleServer();

        registerApplication(server);

        server.start();
        Thread.currentThread().join();
        server.shutdown();
    }

    public static void registerApplication(HttpServer server) {
        WebSocketEngine.getEngine().register("", "/ws", new WebSocketApplication() {
            @Override
            public void onMessage(WebSocket socket, String text) {
                System.out.println(text);
                socket.send(text);
            }

            @Override
            public void onConnect(WebSocket socket) {
                System.out.println("Client connected");
                socket.send("Hi");
            }

        });

        server.getListener("grizzly").registerAddOn(new WebSocketAddOn());
    }
}
