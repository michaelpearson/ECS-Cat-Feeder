package catfeeder.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@XmlAccessorType(XmlAccessType.NONE)
@DatabaseTable(tableName = "event_log")
public class LogEntry {
    @XmlElement(name = "eventId")
    @DatabaseField(generatedId = true)
    private int id;

    @XmlElement
    @DatabaseField
    private Date eventGeneratedAt;

    @ForeignCollectionField
    private Collection<ScheduledFoodDelivery> scheduledFoodDeliveries;

    @ForeignCollectionField
    private Collection<FoodDelivery> foodDeliveries;

    @DatabaseField(foreign = true)
    private CatFeeder feeder;

    @XmlElement
    @DatabaseField
    private EventType eventType;

    public enum EventType {
        ScheduledFoodDelivery(0, "Scheduled delivery", null),
        FoodDelivery(1, "One off delivery", null),
        Disconnection(2, "Device disconnected", Notification.CommonNotificationImages.DISCONNECTED),
        Connection(3, "Device connected", Notification.CommonNotificationImages.CONNECTED),
        DoorsOpen(4, "Door opened", null),
        UnauthorizedAccessAttempt(5, "Unauthorized access attempt", null),
        FoodDeliveryTimeout(6, "Delivery timed out", Notification.CommonNotificationImages.TIMEOUT),
        MaxWeightReached(7, "Maximum weight in bowl reached", Notification.CommonNotificationImages.FULL);

        private String jsonString;
        private int eventId;
        private final Notification.CommonNotificationImages image;

        EventType(int eventId, String value, Notification.CommonNotificationImages image) {
            this.jsonString = value;
            this.eventId = eventId;
            this.image = image;
        }

        public static EventType fromEventId(int eventId) {
            return Arrays.stream(values()).filter(e -> e.eventId == eventId).findFirst().orElse(null);
        }

        public int getEventId() {
            return eventId;
        }

        @JsonValue
        public String getEventName() {
            return jsonString;
        }

        public Notification.CommonNotificationImages getImage() {
            return image;
        }
    }

    @XmlElement(name = "information")
    public Object getEvent() {
        switch(eventType) {
            case ScheduledFoodDelivery:
                return scheduledFoodDeliveries.iterator().next();
            case FoodDelivery:
                return foodDeliveries.iterator().next();
            default:
                return null;
        }
    }

    public int getId() {
        return id;
    }

    public Date getEventGeneratedAt() {
        return eventGeneratedAt;
    }

    public void setEventGeneratedAt(Date eventGeneratedAt) {
        this.eventGeneratedAt = eventGeneratedAt;
    }

    public CatFeeder getFeeder() {
        return feeder;
    }

    public void setFeeder(CatFeeder feeder) {
        this.feeder = feeder;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
