package catfeeder.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@DatabaseTable(tableName = "schedule")
public class Schedule {

    @XmlEnum
    public enum DayOfWeek implements Serializable {
        @XmlEnumValue("Monday")
        MONDAY,
        @XmlEnumValue("Tuesday")
        TUESDAY,
        @XmlEnumValue("Wednesday")
        WEDNESDAY,
        @XmlEnumValue("Thursday")
        THURSDAY,
        @XmlEnumValue("Friday")
        FRIDAY,
        @XmlEnumValue("Saturday")
        SATURDAY,
        @XmlEnumValue("Sunday")
        SUNDAY
    }
    @XmlElement
    @DatabaseField(generatedId = true)
    private int id;
    @XmlTransient
    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
    private CatFeeder feeder;
    @XmlElement
    @DatabaseField
    private int gramAmount;
    @XmlElement
    @DatabaseField
    private boolean recurring;
    @XmlElement
    @DatabaseField
    private Date firstDelivery;
    @XmlElement
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private DayOfWeek[] daysOfWeek;
    @XmlElement
    @DatabaseField
    private Date endDate;
    @XmlElement
    @DatabaseField
    private int foodIndex;
    @XmlElement(name = "deliveries")
    private List<FoodDelivery> derivedDeliveries;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CatFeeder getFeeder() {
        return feeder;
    }

    public void setFeeder(CatFeeder feeder) {
        this.feeder = feeder;
    }

    public int getGramAmount() {
        return gramAmount;
    }

    public void setGramAmount(int gramAmount) {
        this.gramAmount = gramAmount;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public Date getFirstDelivery() {
        return firstDelivery;
    }

    public void setFirstDelivery(Date firstDelivery) {
        this.firstDelivery = firstDelivery;
    }

    public DayOfWeek[] getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(DayOfWeek[] daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getFoodIndex() {
        return foodIndex;
    }

    public void setFoodIndex(int foodIndex) {
        this.foodIndex = foodIndex;
    }

    public List<FoodDelivery> getDerivedDeliveries() {
        return derivedDeliveries;
    }

    public void populateDeliveries(int month, int year) {
        Calendar deliveryStartDate = Calendar.getInstance();
        deliveryStartDate.setTime(firstDelivery);
        if(!recurring) {
            ArrayList<FoodDelivery> r = new ArrayList<>();
            if(year != deliveryStartDate.get(Calendar.YEAR) || month != deliveryStartDate.get(Calendar.MONTH) + 1) {
                this.derivedDeliveries = r;
                return;
            }
            FoodDelivery d = new FoodDelivery();
            d.setGramAmount(gramAmount);
            d.setDateTime(firstDelivery);
            r.add(d);
            this.derivedDeliveries = r;
            return;
        }
        throw new RuntimeException("Haven't implemented scheduling yet");
    }
}
