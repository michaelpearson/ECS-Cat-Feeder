package catfeeder.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.NONE)
@DatabaseTable(tableName = "foodtype")
public class FoodType {
    @XmlElement
    @DatabaseField(generatedId = true)
    private int id;

    @XmlElement
    @DatabaseField
    private String name;

    @XmlTransient
    @DatabaseField(foreign = true)
    private CatFeeder catfeeder;

    @DatabaseField
    private int foodIndex;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CatFeeder getCatfeeder() {
        return catfeeder;
    }

    public void setCatfeeder(CatFeeder catfeeder) {
        this.catfeeder = catfeeder;
    }

    public int getFoodIndex() {
        return foodIndex;
    }

    public void setFoodIndex(int foodIndex) {
        this.foodIndex = foodIndex;
    }
}
