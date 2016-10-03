package catfeeder.notifications;

import catfeeder.db.DatabaseClient;
import catfeeder.model.Notification;
import catfeeder.model.User;

import java.sql.SQLException;

public class NotificationBuilderFactory {

    public static abstract class NotificationBuilder {

        protected String message, subject;
        protected User user;

        public NotificationBuilder setMessageBody(String message) {
            this.message = message;
            return this;
        }

        public NotificationBuilder setMessageSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public NotificationBuilder setRecipient(User user) {
            this.user = user;
            return this;
        }

        public abstract Notification build() throws RuntimeException, SQLException;
    }

    private static class SmsNotification extends NotificationBuilder {
        @Override
        public Notification build() throws RuntimeException, SQLException {
            Notification notification = new Notification(message, user, subject, Notification.NotificationType.SMS);
            DatabaseClient.getNotificationDao().create(notification);
            return notification;
        }
    }

    private static class EmailNotification extends NotificationBuilder {
        @Override
        public Notification build() throws RuntimeException, SQLException {
            Notification notification = new Notification(message, user, subject, Notification.NotificationType.EMAIL);
            DatabaseClient.getNotificationDao().create(notification);
            return notification;
        }
    }

    public static NotificationBuilder getInstance(Notification.NotificationType notificationType) {
        switch(notificationType) {
            default:
                throw new RuntimeException("Notification type not supported");
            case EMAIL:
                return new EmailNotification();
            case SMS:
                return new SmsNotification();
        }
    }

}
