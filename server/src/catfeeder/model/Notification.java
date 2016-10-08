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

    @XmlElement
    @DatabaseField
    private boolean seen;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    protected User user;

    @XmlElement
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private LogEntry logEntry;

    @XmlElement
    @DatabaseField
    private String image;

    public enum CommonNotificationImages {
        TIMEOUT("/assets/images/timeout.png"),
        FULL("/assets/images/full.png"),
        DISCONNECTED("/assets/images/disconnected.png"),
        CONNECTED("/assets/images/connected.png");

        private final String imageUrl;

        CommonNotificationImages(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getUrl() {
            return imageUrl;
        }
    }

    public Notification() {}

    public Notification(String notificationBody, User user, String subject, String image, LogEntry logEntry) {
        this.notificationBody = notificationBody;
        this.user = user;
        this.subject = subject;
        this.date = new Date();
        this.sent = false;
        this.seen = false;
        this.image = image;
        this.logEntry = logEntry;
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

    public void setSeen() {
        seen = true;
    }

    public boolean isSeen() {
        return seen;
    }

    public boolean isSent() {
        return sent;
    }
}
