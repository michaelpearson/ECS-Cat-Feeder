package catfeeder.feeder;

import catfeeder.db.DatabaseClient;
import catfeeder.model.CatFeeder;
import catfeeder.model.FoodType;
import catfeeder.model.ScheduledItem;
import catfeeder.model.Tag;
import catfeeder.model.response.CardInfo;
import com.j256.ormlite.dao.Dao;
import org.glassfish.grizzly.websockets.WebSocket;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class CatFeederConnection {
    private final WebSocket socket;
    private final CatFeeder feeder;
    private final Object messageLock = new Object();
    private final Calendar calendar = Calendar.getInstance();

    private JSONObject lastMessage = null;
    private ScheduledItem nextScheduledEvent = null;

    WebSocket getWebSocket() {
        return socket;
    }

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


    CatFeederConnection(WebSocket socket, int catFeederId) throws SQLException {
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
        updateAlarm();
    }

    void onMessage(JSONObject data) {
        System.out.println("Got message: " + data);
        if(data.containsKey("command")) {
            handleCommand(data);
        } else {
            lastMessage = data;
            synchronized (messageLock) {
                messageLock.notify();
            }
        }
    }

    private void handleCommand(JSONObject data) {}

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
        return new CardInfo((boolean)message.get("is_present"), (long)message.get("card_id"));
    }

    public void setTrustedTag(Tag tag) {
        JSONObject payload = new JSONObject();
        payload.put("command", Commands.SetTrustedTag.getCommandId());
        payload.put("tag_uid", tag.getTagUID());
        socket.send(payload.toJSONString());
    }

    long getFeederHardwareId() {
        return feeder.getHardwareId();
    }

    public void updateAlarm() throws SQLException {
        calendar.setTime(new Date());
        ScheduleManager scheduleManager = new ScheduleManager(DatabaseClient.getScheduleDao(), feeder, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        for(ScheduledItem item : scheduleManager) {
            if(item.getDateTime().after(new Date())) {
                nextScheduledEvent = item;
                AlarmManager.registerAlarm(nextScheduledEvent.getDateTime(), this::alarm);
                return;
            }
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        AlarmManager.registerAlarm(calendar.getTime(), this::alarm);
    }

    private synchronized void alarm() {
        if(!getWebSocket().isConnected() || nextScheduledEvent == null) {
            return;
        }
        if(nextScheduledEvent.getDateTime().getTime() - System.currentTimeMillis() <= 0) {
            nextScheduledEvent.execute(this);
        }
        nextScheduledEvent = null;
    }

}
