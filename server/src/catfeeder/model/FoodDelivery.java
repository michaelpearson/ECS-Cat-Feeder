package catfeeder.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@DatabaseTable(tableName = "food_deliveries")
public class FoodDelivery {
    @DatabaseField(id = true)
    private int id;
    @XmlElement(name = "date")
    @DatabaseField
    private Date dateTime;
    @DatabaseField
    private int gramAmount;
    @DatabaseField(foreign = true, canBeNull = false)
    private CatFeeder feeder;
    @XmlElement
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Schedule scheduledDelivery;

    public Date getDateTime() {
        return dateTime;
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

    public CatFeeder getFeeder() {
        return feeder;
    }

    public void setFeeder(CatFeeder feeder) {
        this.feeder = feeder;
    }

    public Schedule getScheduledDelivery() {
        return scheduledDelivery;
    }

    public void setScheduledDelivery(Schedule scheduledDelivery) {
        this.scheduledDelivery = scheduledDelivery;
    }
}
