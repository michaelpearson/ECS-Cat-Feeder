package catfeeder.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

@XmlAccessorType(XmlAccessType.NONE)
@DatabaseTable(tableName = "notifications")
public class Notification {

    public enum NotificationType {
        SMS,
        EMAIL
    }

    @DatabaseField
    @XmlElement
    private NotificationType notificationType;

    @DatabaseField(generatedId = true)
    private int id;

    @XmlElement
    @DatabaseField
    protected String notificationBody;

    @XmlElement
    @DatabaseField
    protected String subject;

    @XmlElement
    @DatabaseField
    private Date date;

    @XmlElement
    @DatabaseField
    private boolean sent;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    protected User user;

    public Notification() {}

    public Notification(String notificationBody, User user, String subject, NotificationType type) {
        this.notificationBody = notificationBody;
        this.user = user;
        this.subject = subject;
        this.date = new Date();
        this.sent = false;
        this.notificationType = type;
    }


    public String getNotificationBody() {
        return notificationBody;
    }

    public String getSubject() {
        return subject;
    }

    public Date getDate() {
        return date;
    }

    public User getUser() {
        return user;
    }

    public void setSent() {
        sent = true;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }
}
