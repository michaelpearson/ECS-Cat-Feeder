package catfeeder.model;

import catfeeder.db.DatabaseClient;
import catfeeder.feeder.CatFeederConnection;
import catfeeder.feeder.SocketManager;
import catfeeder.feeder.response.CardInfo;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.sql.SQLException;
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

    @ForeignCollectionField(orderColumnName = "eventGeneratedAt")
    private Collection<LogEntry> logEntries;

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

    private boolean deliverFood(int amount, FoodType foodType, Schedule schedule) throws SQLException {
        CatFeederConnection connection = getFeederConnection();
        if(connection == null) {
            return false;
        }
        if(foodType == null) {
            return false;
        }
        connection.deliverFood(amount, foodType);

        LogEntry entry = new LogEntry();
        entry.setFeeder(this);
        entry.setEventGeneratedAt(new Date());

        DatabaseClient.getLogEntryDao().create(entry);

        if(schedule != null) {
            ScheduledFoodDelivery delivery = new ScheduledFoodDelivery();
            delivery.setScheduledDelivery(schedule);
            delivery.setGramAmount(amount);
            delivery.setDateTime(new Date());
            delivery.setLogEntry(entry);
            DatabaseClient.getScheduledFoodDeliveryDao().create(delivery);
        } else {
            FoodDelivery delivery = new FoodDelivery();
            delivery.setGramAmount(amount);
            delivery.setDateTime(new Date());
            delivery.setLogEntry(entry);
            delivery.setFoodType(foodType);
            DatabaseClient.getFoodDeliveryDao().create(delivery);
        }
        return true;

    }

    public boolean deliverFood(int amount, Schedule schedule) throws SQLException {
        return deliverFood(amount, schedule.getFoodType(), schedule);
    }

    public boolean deliverFood(int amount, FoodType foodType) throws SQLException {
        return deliverFood(amount, foodType, null);
    }

    public CardInfo getLastCardInfo() {
        CatFeederConnection connection = getFeederConnection();
        if(connection == null) {
            return null;
        }
        return connection.queryLastCardId();
    }

    private CatFeederConnection getFeederConnection() {
        return SocketManager.getCatfeederConnection(getHardwareId());
    }

    public Collection<LogEntry> getLogEntries() {
        return logEntries;
    }
}
