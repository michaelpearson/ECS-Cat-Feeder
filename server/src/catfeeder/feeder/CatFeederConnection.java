package catfeeder.feeder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CatFeederConnection extends Thread {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public CatFeederConnection(Socket socket) {
        this.socket = socket;
        try {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            start();
            System.out.println("Accepted new connection");
        } catch (IOException ignore) {}
    }


    @Override
    public void run() {
        try {
            while(socket.isConnected()) {
                System.out.printf("GOT: %d\n", readI32());
            }
        } catch(IOException e) {
            System.err.println("Connection died");
        }
    }

    private int readI32() throws IOException {
        int number = inputStream.read() & 0xFF;
        number |= (inputStream.read() & 0xFF) << (1 * 8);
        number |= (inputStream.read() & 0xFF) << (2 * 8);
        number |= (inputStream.read() & 0xFF) << (3 * 8);
        return number;
    }

    public void sendStatusCommand() throws IOException {
        outputStream.write(new byte[] {0x00, 0x00, 0x00, 0x00});
    }

}
