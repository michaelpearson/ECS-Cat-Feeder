package catfeeder.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@DatabaseTable(tableName = "schedule")
public class Schedule {

    public int getFoodIndex() {
        return foodIndex;
    }

    public void setFoodIndex(int foodIndex) {
        this.foodIndex = foodIndex;
    }

    public enum DayOfWeek implements Serializable {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField(foreign = true, canBeNull = false)
    private CatFeeder feeder;
    @DatabaseField
    private int gramAmount;
    @DatabaseField
    private boolean recurring;
    @DatabaseField
    private Date firstDelivery;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private DayOfWeek[] daysOfWeek;
    @DatabaseField
    private Date endDate;
    @DatabaseField
    private int foodIndex;


    public long getId() {
        return id;
    }

    public void setId(long id) {
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


    public List<FoodDelivery> getDeliveriesForMonth(int year, int month) {
        Calendar deliveryStartDate = Calendar.getInstance();
        deliveryStartDate.setTime(firstDelivery);
        if(!recurring) {
            ArrayList<FoodDelivery> r = new ArrayList();
            if(year != deliveryStartDate.get(Calendar.YEAR) || month != deliveryStartDate.get(Calendar.MONTH) + 1) {
                return r;
            }
            FoodDelivery d = new FoodDelivery();
            d.setGramAmount(gramAmount);
            d.setDateTime(firstDelivery);
            r.add(d);
            return r;
        }
        throw new RuntimeException("Haven't implemented scheduling yet");
    }
}
