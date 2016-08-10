package catfeeder.feeder;

import catfeeder.db.DatabaseClient;
import catfeeder.feeder.response.CardInfo;
import catfeeder.model.CatFeeder;
import catfeeder.model.FoodType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;


public class CatFeederConnection extends Thread {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private CatFeeder feeder;
    private Queue<Byte> commandQueue = new LinkedList<>();

    private final Object lock = new Object();

    public CatFeederConnection(Socket socket) {
        this.socket = socket;
        try {
            socket.setSoTimeout(1000);
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            int feederId = readI32();
            this.feeder = DatabaseClient.getFeederDao().queryForId(feederId);
            if(feeder != null) {
                start();
            } else {
                System.out.printf("Unknown cat feeder %d", feederId);
            }
        } catch (IOException | SQLException ignore) {}
    }


    @Override
    public void run() {
        System.out.println("Accepted new connection");
        try {
            while(socket.isConnected() && !interrupted()) {
                while (commandQueue.isEmpty()) {
                    synchronized (lock) {
                        lock.wait();
                    }
                    if (!socket.isConnected()) {
                        throw new IOException();
                    }
                }
                int i = 0;
                Byte b;
                while ((b = commandQueue.poll()) != null) {
                    outputStream.write(b);
                    i++;
                }
                System.out.println("Written " + i + " bytes");
                outputStream.flush();
            }
        } catch(IOException | InterruptedException e) {
            System.err.println("Connection died");
        }
        System.err.println("Socked dead");
    }

    synchronized boolean checkConnection() {
        try {
            outputStream.write(0x03);
            if(inputStream.read() != 0x03) {
                throw new IOException();
            }
        } catch (IOException e) {
            shutdown();
            return false;
        }
        return true;
    }

    public void shutdown() {
        try {
            socket.close();
            interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized int readI32() throws IOException {
        try {
            int number = inputStream.read() & 0xFF;
            number |= (inputStream.read() & 0xFF) << (1 * 8);
            number |= (inputStream.read() & 0xFF) << (2 * 8);
            number |= (inputStream.read() & 0xFF) << (3 * 8);
            return number;
        } catch (IOException e) {
            socket.close();
            throw e;
        }
    }

    public synchronized void deliverFood(int gramAmount, FoodType foodType) {
        commandQueue.add((byte)0x01); //Deliver food command
        addIntToQueue(commandQueue, gramAmount);
        addIntToQueue(commandQueue, foodType.getFoodIndex());
        pushNotification();
    }

    private static void addIntToQueue(Queue<Byte> queue, int value) {
        queue.add((byte)(value & 0xFF));
        queue.add((byte)((value >> 8) & 0xFF));
        queue.add((byte)((value >> 16) & 0xFF));
        queue.add((byte)((value >> 24) & 0xFF));
    }

    /**
     * Used to wakeup the sender thread to push the command queue to the socket
     */
    private void pushNotification() {
        synchronized (lock) {
            lock.notify();
            System.out.println("Notified");
        }
    }

    public synchronized CardInfo queryLastCardId() {
        commandQueue.add((byte)0x02); //Query for last card id
        pushNotification();
        try {
            long id = readI32() & 0xFFFFFFFFL;
            boolean present = readI32() > 0;
            return new CardInfo(present, id);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public long getFeederHardwareId() {
        return feeder.getHardwareId();
    }
}
