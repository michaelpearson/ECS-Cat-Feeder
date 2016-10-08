package catfeeder.notifications;

import catfeeder.model.CatFeeder;
import catfeeder.model.LogEntry;
import catfeeder.model.Notification;
import catfeeder.model.User;

import java.sql.SQLException;

public class NotificationService {

    private final CatFeeder feeder;

    public NotificationService(CatFeeder feeder) {
        this.feeder = feeder;
    }

    public void sendNotification(String message, String subject, LogEntry logEntry) {
        feeder.getOwners().forEach(owner -> {
            try {
                Notification n = NotificationBuilderFactory.getInstance()
                        .setMessageBody(message)
                        .setMessageSubject(subject)
                        .setLogEntry(logEntry)
                        .setImage(logEntry.getEventType().getImage() != null ? logEntry.getEventType().getImage().getUrl() : null)
                        .setRecipient(owner.getUser())
                        .build();
                dispatchNotification(n);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void dispatchNotification(Notification notification) {
        User u = notification.getUser();
        u.getNotificationTypes().forEach(type -> type.getSendStrategy().sendNotification(notification));
    }

}
