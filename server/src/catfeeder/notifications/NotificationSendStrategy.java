package catfeeder.notifications;

import catfeeder.model.Notification;

public interface NotificationSendStrategy {
    boolean sendNotification(Notification notification);
}
