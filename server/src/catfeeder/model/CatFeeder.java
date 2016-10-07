package catfeeder.model;

import catfeeder.db.DatabaseClient;
import catfeeder.exceptions.FeederNotConnected;
import catfeeder.feeder.CatFeederConnection;
import catfeeder.feeder.CatfeederSocketApplication;
import catfeeder.feeder.EventLogger;
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

    @XmlElement
    @DatabaseField(persisterClass = LearnStage.Persister.class)
    private LearnStage learningStage;

    @ForeignCollectionField
    @XmlTransient
    private ForeignCollection<Schedule> scheduledDeliveries;

    @XmlTransient
    @ForeignCollectionField
    private Collection<FeederUserConnection> owners;

    @ForeignCollectionField
    @XmlElement
    private Collection<FoodType> foodTypes;

    @ForeignCollectionField(orderColumnName = "eventGeneratedAt", orderAscending = false)
    private Collection<LogEntry> logEntries;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Tag trustedTag;

    @ForeignCollectionField
    private Collection<Tag> tags;

    @DatabaseField
    @XmlElement
    private int foodLimit = 500;

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

    public LearnStage getLearningStage(){ return learningStage;}

    public void setLearningStage(LearnStage stage){ learningStage = stage;}

    public ForeignCollection<Schedule> getScheduledDeliveries() {
        return scheduledDeliveries;
    }

    public void setScheduledDeliveries(ForeignCollection<Schedule> scheduledDeliveries) {
        this.scheduledDeliveries = scheduledDeliveries;
    }

    public Collection<FoodType> getFoodTypes() {
        return foodTypes;
    }

    public boolean isValidFoodType(FoodType foodType) {
        return foodTypes.stream().anyMatch(ft -> ft.getId() == foodType.getId());
    }

    private boolean deliverFood(int amount, FoodType foodType, Schedule schedule) throws SQLException, FeederNotConnected {
        CatFeederConnection connection = getFeederConnection();
        if(foodType == null) {
            return false;
        }
        connection.deliverFood(amount, foodType);

        LogEntry entry = getEventLogger().logEvent(schedule == null ? LogEntry.EventType.FoodDelivery : LogEntry.EventType.ScheduledFoodDelivery);

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

    public boolean deliverFood(int amount, Schedule schedule) throws SQLException, FeederNotConnected {
        return deliverFood(amount, schedule.getFoodType(), schedule);
    }

    public EventLogger getEventLogger() {
        return eventType -> {
            LogEntry entry = new LogEntry();
            entry.setFeeder(CatFeeder.this);
            entry.setEventGeneratedAt(new Date());
            entry.setEventType(eventType);
            DatabaseClient.getLogEntryDao().create(entry);
            return entry;
        };
    }

    public boolean deliverFood(int amount, FoodType foodType) throws SQLException, FeederNotConnected {
        return deliverFood(amount, foodType, null);
    }

    public CardInfo getLastCardInfo() throws FeederNotConnected, SQLException, InterruptedException {
        CatFeederConnection connection = getFeederConnection();
        return connection.queryLastCardId();
    }

    private CatFeederConnection getFeederConnection() throws FeederNotConnected {
        CatFeederConnection connection = CatfeederSocketApplication.getCatfeederConnection(getHardwareId());
        if(connection == null) {
            throw new FeederNotConnected();
        }
        return connection;
    }

    public Collection<LogEntry> getLogEntries() {
        return logEntries;
    }

    public Tag getTrustedTag() {
        return trustedTag;
    }

    public void setTrustedTag(Tag trustedTag) throws FeederNotConnected {
        CatFeederConnection connection = getFeederConnection();
        connection.setTrustedTag(trustedTag);
        this.trustedTag = trustedTag;
    }

    @Override
    public String toString() {
        return String.format("Cat feeder: %d", getHardwareId());
    }

    public Collection<Tag> getTags() {
        return tags;
    }

    public void setTags(Collection<Tag> tags) {
        this.tags = tags;
    }

    public Collection<FeederUserConnection> getOwners() {
        return owners;
    }

    public int getFoodLimit() {
        return foodLimit;
    }

    public void setFoodLimit(int foodLimit) {
        this.foodLimit = foodLimit;
    }

}
