package catfeeder.notifications;

import catfeeder.db.DatabaseClient;
import catfeeder.model.Notification;

import java.sql.SQLException;

public class NotificationSender {

    private final Notification notification;

    public NotificationSender(Notification notification) {
        this.notification = notification;
    }

    public boolean sendNotification() throws SQLException {
        if(getStrategy(notification).sendNotification(notification)) {
            notification.setSent();
            DatabaseClient.getNotificationDao().update(notification);
            return true;
        }
        return false;
    }

    private NotificationSendStrategy getStrategy(Notification notification) {
        return getStrategy(notification.getNotificationType());
    }

    private NotificationSendStrategy getStrategy(Notification.NotificationType type) {
        switch(type) {
            default:
                throw new RuntimeException("Notification type not supported");
            case EMAIL:
                return new EmailSender();
            case SMS:
                return new SmsSender();
        }
    }


}
