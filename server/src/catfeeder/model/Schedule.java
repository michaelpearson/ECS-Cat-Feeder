package catfeeder.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.NONE)
@DatabaseTable(tableName = "schedule")
public class Schedule {

    private static Calendar calendar = Calendar.getInstance();

    @XmlEnum
    public enum DayOfWeek implements Serializable {
        MONDAY("Monday", Calendar.MONDAY),
        TUESDAY("Tuesday", Calendar.TUESDAY),
        WEDNESDAY("Wednesday", Calendar.WEDNESDAY),
        THURSDAY("Thursday", Calendar.THURSDAY),
        FRIDAY("Friday", Calendar.FRIDAY),
        SATURDAY("Saturday", Calendar.SATURDAY),
        SUNDAY("Sunday", Calendar.SUNDAY);
        private String value;
        private int calendarDay;

        @JsonValue
        public String getValue() {
            return value;
        }

        public int getCalendarDay() {
            return calendarDay;
        }

        DayOfWeek(String value, int calendarDay) {
            this.value = value;
            this.calendarDay = calendarDay;
        }

        @Override
        public String toString() {
            return value;
        }

        public static DayOfWeek fromString(String d) {
            return Arrays.stream(DayOfWeek.values()).filter(day -> day.value.equals(d)).findFirst().orElse(null);
        }
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
    private Date startDate;

    @XmlElement
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<DayOfWeek> daysOfWeek;

    @XmlElement
    @DatabaseField
    private Date endDate;

    @XmlElement
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private FoodType foodType;

    @XmlElement(name = "deliveries")
    private List<ScheduledFoodDelivery> derivedDeliveries;

    @XmlElement
    @DatabaseField
    private String notes;


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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public ArrayList<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(ArrayList<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        if(getFeeder() == null || !getFeeder().isValidFoodType(foodType)) {
            return;
        }
        this.foodType = foodType;
    }

    public void setDerivedDeliveries(List<ScheduledFoodDelivery> derivedDeliveries) {
        this.derivedDeliveries = derivedDeliveries;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<ScheduledFoodDelivery> getDerivedDeliveries() {
        return derivedDeliveries;
    }

    public void populateDeliveries(int month, int year) {
        Calendar deliveryStartDate = Calendar.getInstance();
        deliveryStartDate.setTime(startDate);
        if(!recurring) {
            ArrayList<ScheduledFoodDelivery> r = new ArrayList<>();
            if(year != deliveryStartDate.get(Calendar.YEAR) || month != deliveryStartDate.get(Calendar.MONTH) + 1) {
                this.derivedDeliveries = r;
                return;
            }
            ScheduledFoodDelivery d = new ScheduledFoodDelivery();
            d.setGramAmount(gramAmount);
            d.setDateTime(startDate);
            r.add(d);
            this.derivedDeliveries = r;
            return;
        }
        this.derivedDeliveries = new ArrayList<>();
        calendar.setTime(getStartDate());
        for(DayOfWeek d : daysOfWeek) {
            List<Date> days = getAllDaysInMonth(d, year, month).stream()
                    .filter(date -> endDate == null || date.before(endDate))
                    .filter(date -> date.after(startDate))
                    .collect(Collectors.toList());
            for(Date date : days) {
                ScheduledFoodDelivery delivery = new ScheduledFoodDelivery();
                delivery.setDateTime(date);
                delivery.setGramAmount(gramAmount);
                derivedDeliveries.add(delivery);
            }
        }
    }

    private synchronized static List<Date> getAllDaysInMonth(DayOfWeek day, int year, int month) {
        List<Date> dates = new LinkedList<>();
        calendar.set(Calendar.DAY_OF_WEEK, day.getCalendarDay());
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
        int i = 1;
        while(calendar.get(Calendar.MONTH) == month - 1) {
            dates.add(calendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, ++i);
        }
        return dates;
    }
}
