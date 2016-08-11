package catfeeder.feeder;

import catfeeder.model.CatFeeder;
import javafx.beans.binding.ListBinding;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SocketManager {
    private static SocketManager singleton;

    private static final int PORT = 6969;
    static List<CatFeederConnection> catFeeders = new ArrayList<>();
    private ServerSocket listener;

    private SocketManager() {}

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

    private Thread livenessThread = new Thread() {
        public void run() {
            while (!listener.isClosed() && !interrupted()) {
                List<CatFeederConnection> connectionsToRemove = new LinkedList<>();
                for (CatFeederConnection c : catFeeders) {
                    if (!c.checkConnection()) {
                        System.err.println("Connection died");
                        connectionsToRemove.add(c);
                    }
                }
                try {
                    sleep(60000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    };

    public static void init() {
        singleton = new SocketManager();
        try {
            singleton.listener = new ServerSocket(PORT);
            singleton.listener.setSoTimeout(1000);
            singleton.serverThread.start();
            singleton.livenessThread.start();
        } catch (IOException e) {
            throw new RuntimeException("Could not start listening for connections");
        }
    }

    public static void shutdown() {
        singleton.serverThread.interrupt();
        singleton.livenessThread.interrupt();
        singleton.livenessThread = null;
        singleton.serverThread = null;
        try {
            singleton.listener.close();
        } catch (IOException ignore) {}
    }

    public static CatFeederConnection getCatfeederConnection(long catfeederId) {
       return catFeeders.stream().filter(f -> f.getFeederHardwareId() == catfeederId).filter(CatFeederConnection::checkConnection).findFirst().orElse(null);
    }
}
