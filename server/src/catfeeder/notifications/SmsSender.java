package catfeeder.notifications;

import catfeeder.model.Notification;

public class SmsSender implements NotificationSendStrategy {

    @Override
    public boolean sendNotification(Notification notification) {
        return false;
    }

}
