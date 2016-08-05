package catfeeder.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "food_deliveries")
public class FoodDelivery {
    @DatabaseField(id = true)
    private long id;
    @DatabaseField
    private Date dateTime;
    @DatabaseField
    private int gramAmount;
    @DatabaseField(foreign = true, canBeNull = false)
    private CatFeeder feeder;
    @DatabaseField(foreign = true, canBeNull = true)
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
