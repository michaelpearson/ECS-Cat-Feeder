package catfeeder.feeder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class CatFeederConnection extends Thread implements CatFeeder {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Queue<Byte> commandQueue = new LinkedList<>();

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
            while(socket.isConnected() && !interrupted()) {
                synchronized (this) {
                    while(commandQueue.isEmpty()) {
                        wait();
                        if(!socket.isConnected()) {
                            return;
                        }
                    }
                }
                int i = 0;
                Byte b;
                while((b = commandQueue.poll()) != null) {
                    outputStream.write(b);
                    i++;
                }
                System.out.println("Written " + i + " bytes");
                outputStream.flush();
            }
        } catch(IOException | InterruptedException e) {
            System.err.println("Connection died");
        }
        SocketManager.catFeeders.remove(this);
    }

    private int readI32() throws IOException {
        int number = inputStream.read() & 0xFF;
        number |= (inputStream.read() & 0xFF) << (1 * 8);
        number |= (inputStream.read() & 0xFF) << (2 * 8);
        number |= (inputStream.read() & 0xFF) << (3 * 8);
        return number;
    }

    @Override
    public void deliverFood(int gramAmount, int foodIndex) {
        commandQueue.add((byte)0x01); //Deliver food command
        addIntToQueue(commandQueue, gramAmount);
        addIntToQueue(commandQueue, foodIndex);
        pushNotification();
    }

    private static void addIntToQueue(Queue<Byte> queue, int value) {
        queue.add((byte)(value & 0xFF));
        queue.add((byte)((value >> 8) & 0xFF));
        queue.add((byte)((value >> 16) & 0xFF));
        queue.add((byte)((value >> 24) & 0xFF));
    }

    private void pushNotification() {
        synchronized(this) {
            notifyAll();
        }
    }
}
