package catfeeder.feeder;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class SocketManager implements ServletContextListener {
    private static final int PORT = 6969;
    static List<CatFeederConnection> catFeeders = new ArrayList<>();
    private ServerSocket listener;

    private Thread serverThread = new Thread() {
        @Override
        public void run() {
            while(!listener.isClosed() && !interrupted()) {
                try {
                    try {
                        Socket socket = listener.accept();
                        catFeeders.add(new CatFeederConnection(socket));
                    } catch(SocketTimeoutException ignore) {}
                } catch (IOException e) {
                    System.err.println("Could not accept connection");
                }
            }
        }
    };

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            listener = new ServerSocket(PORT);
            listener.setSoTimeout(1000);
            serverThread.start();
        } catch (IOException e) {
            throw new RuntimeException("Could not start listening for connections");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        serverThread.interrupt();
        serverThread = null;
        try {
            listener.close();
        } catch (IOException ignore) {}
    }

    public static CatFeederConnection getCatfeederConnection(long catfeederId) {
        if(catFeeders.size() == 0) {
            return null;
        }
        return catFeeders.get(0);
    }

}
