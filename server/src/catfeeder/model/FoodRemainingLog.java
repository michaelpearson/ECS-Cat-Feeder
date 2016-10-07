package catfeeder.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "food_remaining_log")
public class FoodRemainingLog {

    @DatabaseField(generatedId = true)
    private int id;


    @DatabaseField(foreign = true)
    private CatFeeder feeder;

    @DatabaseField
    private Date entryDate;

    @DatabaseField
    private int weight;

    public FoodRemainingLog() {}

    public FoodRemainingLog(CatFeeder feeder, Date entryDate, int weight) {
        this.feeder = feeder;
        this.entryDate = entryDate;
        this.weight = weight;
    }

    public Date getDate() {
        return entryDate;
    }

    public int getWeight() {
        return weight;
    }

    public void setFeeder(CatFeeder feeder) {
        this.feeder = feeder;
    }
}
