package catfeeder.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

@XmlAccessorType(XmlAccessType.NONE)
@DatabaseTable(tableName = "food_delivery")
public class FoodDelivery {
    @XmlElement(name = "deliveryId")
    @DatabaseField(generatedId = true)
    private int id;

    @XmlElement(name = "date")
    @DatabaseField
    private Date dateTime;

    @XmlElement
    @DatabaseField
    private int gramAmount;

    @XmlElement
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private FoodType foodType;

    @DatabaseField(foreign = true)
    private LogEntry logEntry;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public int getGramAmount() {
        return gramAmount;
    }

    public void setGramAmount(int gramAmount) {
        this.gramAmount = gramAmount;
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(LogEntry logEntry) {
        this.logEntry = logEntry;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
    }
}
