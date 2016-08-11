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

    @XmlElement
    @ForeignCollectionField
    private Collection<ScheduledFoodDelivery> scheduledFoodDeliveries;

    @XmlElement
    @ForeignCollectionField
    private Collection<FoodDelivery> foodDeliveries;

    @DatabaseField(foreign = true)
    private CatFeeder feeder;

    public enum EventType {
        FoodDelivery,
        Disconnection,
        Connection;
        @JsonValue
        public String getEventType() {
            return this.toString();
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
}
