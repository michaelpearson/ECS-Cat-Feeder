package catfeeder.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Collection;
import java.util.Date;

@XmlAccessorType(XmlAccessType.NONE)
@DatabaseTable(tableName = "cat_feeders")
public class CatFeeder {
    @XmlElement
    @DatabaseField(id = true)
    private int hardwareId;

    @XmlElement
    @DatabaseField
    private String name;

    @XmlElement
    @DatabaseField
    private Date lastConnectionAt;

    @ForeignCollectionField
    @XmlTransient
    private ForeignCollection<Schedule> scheduledDeliveries;

    @XmlTransient
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User owner;

    @ForeignCollectionField
    @XmlElement
    private Collection<FoodType> foodTypes;

    public int getHardwareId() {
        return hardwareId;
    }

    public void setHardwareId(int hardwareId) {
        this.hardwareId = hardwareId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastConnectionAt() {
        return lastConnectionAt;
    }

    public void setLastConnectionAt(Date lastConnectionAt) {
        this.lastConnectionAt = lastConnectionAt;
    }

    public ForeignCollection<Schedule> getScheduledDeliveries() {
        return scheduledDeliveries;
    }

    public void setScheduledDeliveries(ForeignCollection<Schedule> scheduledDeliveries) {
        this.scheduledDeliveries = scheduledDeliveries;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Collection<FoodType> getFoodTypes() {
        return foodTypes;
    }

    public boolean isValidFoodType(FoodType foodType) {
        return foodTypes.stream().anyMatch(ft -> ft.getId() == foodType.getId());
    }
}
