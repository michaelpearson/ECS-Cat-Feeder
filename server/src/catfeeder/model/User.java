package catfeeder.model;

import catfeeder.notifications.EmailSender;
import catfeeder.notifications.NotificationSendStrategy;
import catfeeder.util.Passwords;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.NONE)
@DatabaseTable(tableName = "users")
public class User {

    public enum NotificationType {
        EMAIL(new EmailSender());

        private final NotificationSendStrategy sendStrategy;

        NotificationType(NotificationSendStrategy strategy) {
            this.sendStrategy = strategy;
        }

        public NotificationSendStrategy getSendStrategy() {
            return sendStrategy;
        }
    }

    @XmlElement
    @DatabaseField(id = true)
    private String email;
    @XmlTransient
    @DatabaseField
    private String password;
    @XmlElement
    @DatabaseField
    private String name;
    @XmlTransient
    @ForeignCollectionField
    private Collection<FeederUserConnection> feedersConnections;
    @XmlElement
    @ForeignCollectionField
    private Collection<Notification> notifications;
    @XmlElement
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<NotificationType> preferredNotificationTypes;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean checkPassword(String password) {
        return Passwords.checkPassword(password, this.password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<CatFeeder> getFeeders() {
        return feedersConnections.stream().map(FeederUserConnection::getFeeder).collect(Collectors.toList());
    }

    public boolean doesUserOwnCatfeeder(CatFeeder feeder) {
        return getFeeders().stream().anyMatch(f -> f.getHardwareId() == feeder.getHardwareId());
    }

    public boolean isSame(User user) {
        return user != null && user.getEmail().equals(getEmail());
    }

    public List<NotificationType> getNotificationTypes() {
        return preferredNotificationTypes;
    }

    public void addNotificationMethod(NotificationType notificationType) {
        if(preferredNotificationTypes == null) {
            preferredNotificationTypes = new ArrayList<>();
        }
        if(preferredNotificationTypes.contains(notificationType)) {
            return;
        }
        preferredNotificationTypes.add(notificationType);
    }
}
