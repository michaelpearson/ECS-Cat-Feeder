package catfeeder.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "chrome_notification_registrations")
public class NotificationRegistrations {

    @DatabaseField(index = true, id = true)
    private String registrationId;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user;

    public NotificationRegistrations() {}

    public NotificationRegistrations(String registrationId, User user) {
        this.registrationId = registrationId;
        this.user = user;
    }

    public String getRegistrationId() {
        return registrationId;
    }
}
