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

    private final Object cardInfoLock = new Object();
    private CardInfo cardInfo = null;

    private enum Commands {
        DELIVER_FOOD (1),
        GET_CARD (2);

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

        cardInfo = new CardInfo((boolean)data.get("is_present"), (long)data.get("card_id"));
        synchronized (cardInfoLock) {
            cardInfoLock.notifyAll();
        }
    }


    public void deliverFood(int gramAmount, FoodType foodType) {
        JSONObject payload = new JSONObject();
        payload.put("command", Commands.DELIVER_FOOD.getCommandId());
        payload.put("gram_amount", gramAmount);
        payload.put("food_type", foodType.getFoodIndex());
        socket.send(payload.toJSONString());
    }

    public CardInfo queryLastCardId() throws InterruptedException {
        JSONObject payload = new JSONObject();
        payload.put("command", Commands.GET_CARD.getCommandId());
        socket.send(payload.toJSONString());

        synchronized (cardInfoLock) {
            cardInfo = null;
            cardInfoLock.wait(5000);
        }
        return cardInfo;

        /*commandQueue.add((byte)0x02); //Query for last card id
        pushNotification();
        try {
            long id = readI32() & 0xFFFFFFFFL;
            boolean present = readI32() > 0;
            return new CardInfo(present, id);
        } catch (IOException e) {
            return null;
        }
        */
    }

    public synchronized void setTrustedTag(Tag tag) {
        /*
        commandQueue.add((byte)0x04); //Set trusted tag
        addIntToQueue(commandQueue, (int)tag.getTagUID());
        pushNotification();
        */
    }

    long getFeederHardwareId() {
        return feeder.getHardwareId();
    }
}
