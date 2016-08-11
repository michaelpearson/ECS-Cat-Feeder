package catfeeder.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
        ScheduledFoodDelivery,
        FoodDelivery,
        Disconnection,
        Connection;
        @JsonValue
        public String getEventType() {
            return this.toString();
        }
    }

    @XmlElement
    public Object getEvent() {
        switch(eventType) {
            case Connection:
                return null;
            case Disconnection:
                return null;
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
