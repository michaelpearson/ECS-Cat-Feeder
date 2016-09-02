package catfeeder.feeder;


import catfeeder.db.DatabaseClient;
import catfeeder.model.CatFeeder;
import catfeeder.model.FoodType;
import catfeeder.model.Tag;
import catfeeder.model.response.CardInfo;
import com.j256.ormlite.dao.Dao;
import org.glassfish.grizzly.websockets.WebSocket;
import org.json.simple.JSONObject;

import java.sql.SQLException;


public class CatFeederConnection {
    private final WebSocket socket;
    private final CatFeeder feeder;

    private JSONObject lastMessage = null;
    private final Object messageLock = new Object();

    private enum Commands {
        DeliverFood(1),
        GetLastCard(2),
        SetTrustedTag(3);

        private int commandId;

        Commands(int commandId) {
            this.commandId = commandId;
        }

        public int getCommandId() {
            return commandId;
        }
    }


    CatFeederConnection(WebSocket socket, int catFeederId) {
        this.socket = socket;
        CatFeeder feeder = null;
        try {
            Dao<CatFeeder, Integer> feederDao = DatabaseClient.getFeederDao();
            feeder = feederDao.queryForId(catFeederId);
            System.out.println("Connected to feeder: " + feeder);
        } catch (SQLException e) {
            e.printStackTrace();
            socket.close();
        }
        this.feeder = feeder;
    }

    void onMessage(JSONObject data) {
        System.out.println("Got message: " + data);
        lastMessage = data;
        synchronized (messageLock) {
            messageLock.notify();
        }
    }

    private JSONObject waitForMessage() throws InterruptedException {
        synchronized (messageLock) {
            messageLock.wait(5000);
            JSONObject message = lastMessage;
            lastMessage = null;
            return message;
        }
    }


    public void deliverFood(int gramAmount, FoodType foodType) {
        JSONObject payload = new JSONObject();
        payload.put("command", Commands.DeliverFood.getCommandId());
        payload.put("gram_amount", gramAmount);
        payload.put("food_type", foodType.getFoodIndex());
        socket.send(payload.toJSONString());
    }

    public CardInfo queryLastCardId() throws InterruptedException {
        JSONObject payload = new JSONObject();
        payload.put("command", Commands.GetLastCard.getCommandId());
        socket.send(payload.toJSONString());

        JSONObject message = waitForMessage();
        if(message == null) {
            socket.close();
            return null;
        }
        CardInfo cardInfo = new CardInfo((boolean)message.get("is_present"), (long)message.get("card_id"));;
        return cardInfo;
    }

    public synchronized void setTrustedTag(Tag tag) {
        JSONObject payload = new JSONObject();
        payload.put("command", Commands.SetTrustedTag.getCommandId());
        payload.put("tag_uid", tag.getTagUID());
        socket.send(payload.toJSONString());
    }

    long getFeederHardwareId() {
        return feeder.getHardwareId();
    }
}
