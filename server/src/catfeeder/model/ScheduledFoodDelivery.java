package catfeeder.model;


import catfeeder.feeder.CatFeederConnection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

@XmlAccessorType(XmlAccessType.NONE)
@DatabaseTable(tableName = "scheduled_food_delivery")
public class ScheduledFoodDelivery implements ScheduledItem {
    @DatabaseField(generatedId = true)
    private int id;
    @XmlElement(name = "date")
    @DatabaseField
    private Date dateTime;
    @XmlElement
    @DatabaseField
    private int gramAmount;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Schedule scheduledDelivery;
    @DatabaseField(foreign = true)
    private LogEntry logEntryEntry;

    public Date getDateTime() {
        return dateTime;
    }

    @Override
    public void execute(CatFeederConnection connection) {
        connection.deliverFood(getGramAmount(), getScheduledDelivery().getFoodType());
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public int getGramAmount() {
        return gramAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGramAmount(int gramAmount) {
        this.gramAmount = gramAmount;
    }

    public Schedule getScheduledDelivery() {
        return scheduledDelivery;
    }

    public void setScheduledDelivery(Schedule scheduledDelivery) {
        this.scheduledDelivery = scheduledDelivery;
    }

    public LogEntry getLogEntryEntry() {
        return logEntryEntry;
    }

    public void setLogEntry(LogEntry logEntryEntry) {
        this.logEntryEntry = logEntryEntry;
    }

    @Override
    public String toString() {
        return String.format("Food delivery: amount, %d, day %s", getGramAmount(), getDateTime().toString());
    }
}
