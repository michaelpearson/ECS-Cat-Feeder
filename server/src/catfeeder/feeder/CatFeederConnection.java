package catfeeder.feeder;

import catfeeder.db.DatabaseClient;
import catfeeder.model.CatFeeder;
import catfeeder.model.FoodType;
import catfeeder.model.ScheduledItem;
import catfeeder.model.Tag;
import catfeeder.model.CardInfo;
import catfeeder.notifications.NotificationService;
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
    private NotificationService notificationService;

    private JSONObject lastMessage = null;
    private ScheduleManager scheduleManager;

    WebSocket getWebSocket() {
        return socket;
    }

    private enum Commands {
        DeliverFood(1),
        GetLastCard(2),
        SetTrustedTag(3),
        ReadWeight(4),
        TareSensor(5);

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
            notificationService = new NotificationService(feeder);
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

    private void handleCommand(JSONObject data) {
        switch((String)data.get("command")) {
            case "max_food_notification":
                notificationService.sendNotification("The maximum amount of food in the bowl has been reached", "Catfeeder notification");
                break;
            default:
                throw new RuntimeException("Unknown command");
        }
    }

    private synchronized JSONObject waitForMessage() throws InterruptedException {
        synchronized (messageLock) {
            messageLock.wait(5000);
            JSONObject message = lastMessage;
            lastMessage = null;
            return message;
        }
    }


    public synchronized void deliverFood(int gramAmount, FoodType foodType) {
        JSONObject payload = new JSONObject();
        payload.put("command", Commands.DeliverFood.getCommandId());
        payload.put("gram_amount", gramAmount);
        payload.put("food_type", foodType.getFoodIndex());
        payload.put("max_amount", foodType.getCatfeeder().getFoodLimit());
        socket.send(payload.toJSONString());
    }

    public synchronized CardInfo queryLastCardId() throws InterruptedException {
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

    public synchronized void setTrustedTag(Tag tag) {
        JSONObject payload = new JSONObject();
        payload.put("command", Commands.SetTrustedTag.getCommandId());
        payload.put("tag_uid", tag.getTagUID());
        socket.send(payload.toJSONString());
    }

    long getFeederHardwareId() {
        return feeder.getHardwareId();
    }

    public synchronized int readWeight() throws InterruptedException {
        JSONObject payload = new JSONObject();
        payload.put("command", Commands.ReadWeight.getCommandId());
        socket.send(payload.toJSONString());
        JSONObject response = waitForMessage();
        if(response == null || response.get("weight") == null) {
            return 0;
        }
        return (int)(long)response.get("weight");
    }

    public void updateAlarm() throws SQLException {
        calendar.setTime(new Date());
        scheduleManager = new ScheduleManager(DatabaseClient.getScheduleDao(), feeder);
        for(ScheduledItem item : scheduleManager) {
            if(item.getDateTime().after(new Date())) {
                AlarmManager.registerAlarm(item.getDateTime(), this::alarm);
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

    public synchronized void tare() {
        JSONObject payload = new JSONObject();
        payload.put("command", Commands.TareSensor.getCommandId());
        socket.send(payload.toJSONString());
    }

    private synchronized void alarm() {
        if(scheduleManager == null || !getWebSocket().isConnected()) {
            return;
        }
        for(ScheduledItem item : scheduleManager) {
            if(item.getDateTime().getTime() - System.currentTimeMillis() > 0) {
                return;
            }
            item.execute(this);
        }
        scheduleManager = null;
    }

}
