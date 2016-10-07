package catfeeder.feeder;

import catfeeder.db.DatabaseClient;
import catfeeder.model.*;
import catfeeder.notifications.NotificationService;
import com.j256.ormlite.dao.Dao;
import org.glassfish.grizzly.websockets.WebSocket;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class CatFeederConnection {
    private static final String NOTIFICATION_SUBJECT = "Catfeeder notification";
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

    public void disconnected() {
        try {
            LogEntry event = feeder.getEventLogger().logEvent(LogEntry.EventType.Disconnection);
            notificationService.sendNotification("Catfeeder disconnected!", NOTIFICATION_SUBJECT, event);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void run(boolean run) {
        JSONObject payload = new JSONObject();
        Commands command = run ? Commands.RunConveyors : Commands.StopConveyors;
        payload.put("command", command.getCommandId());
        socket.send(payload.toJSONString());
    }

    private enum Commands {
        DeliverFood(1),
        GetLastCard(2),
        SetTrustedTag(3),
        ReadWeight(4),
        TareSensor(5),
        RunConveyors(6),
        StopConveyors(7),
        SetMode(8);

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
            LogEntry event = feeder.getEventLogger().logEvent(LogEntry.EventType.Connection);
            notificationService = new NotificationService(feeder);
            notificationService.sendNotification("Catfeeder connected!", NOTIFICATION_SUBJECT, event);
            System.out.println("Connected to feeder: " + feeder);
            setMode(feeder.getLearningStage());
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
            try {
                handleCommand(data);
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else {
            lastMessage = data;
            synchronized (messageLock) {
                messageLock.notify();
            }
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

    private void handleCommand(JSONObject data) throws SQLException {
        switch((String)data.get("command")) {
            case "max_food_notification": {
                LogEntry event = feeder.getEventLogger().logEvent(LogEntry.EventType.MaxWeightReached);
                notificationService.sendNotification("The maximum amount of food in the bowl has been reached", NOTIFICATION_SUBJECT, event);
                break;
            }
            case "food_timeout_notification": {
                long index = (long) data.get("food_index");
                FoodType foodType = feeder.getFoodTypes().stream().filter(ft -> ft.getFoodIndex() == index).findFirst().orElse(null);
                String foodName = "unknown";
                if (foodType != null) {
                    foodName = foodType.getName();
                }
                LogEntry event = feeder.getEventLogger().logEvent(LogEntry.EventType.FoodDeliveryTimeout);
                notificationService.sendNotification("The feeder timed out while delivering " + foodName, NOTIFICATION_SUBJECT, event);
                break;
            }
            case "log_weight": {
                int weight = Math.toIntExact((long) data.get("weight"));
                FoodRemainingLog entry = new FoodRemainingLog(feeder, new Date(), weight);
                DatabaseClient.getFoodRemainingLogDao().create(entry);
                break;
            }
            case "log_doors": {
                LogEntry.EventType type = (boolean)data.get("access") ? LogEntry.EventType.DoorsOpen : LogEntry.EventType.UnauthorizedAccessAttempt;
                feeder.getEventLogger().logEvent(type);
                break;
            }
            default:
                throw new RuntimeException("Unknown command");
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

    public synchronized void tare() {
        JSONObject payload = new JSONObject();
        payload.put("command", Commands.TareSensor.getCommandId());
        socket.send(payload.toJSONString());
    }

    public synchronized void setMode(LearnStage mode) {
        JSONObject payload = new JSONObject();
        payload.put("command", Commands.SetMode.getCommandId());
        payload.put("mode", mode.getStageId());
        socket.send(payload.toJSONString());
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
