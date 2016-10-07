package catfeeder.notifications;

import catfeeder.db.DatabaseClient;
import catfeeder.model.LogEntry;
import catfeeder.model.Notification;
import catfeeder.model.User;

import java.sql.SQLException;

public class NotificationBuilderFactory {

    public interface NotificationBuilder {
        NotificationBuilder setMessageBody(String message);
        NotificationBuilder setMessageSubject(String subject);
        NotificationBuilder setRecipient(User user);
        NotificationBuilder setLogEntry(LogEntry logEntry);
        Notification build() throws RuntimeException, SQLException;
    }

    public static class NotificationBuilderImpl implements NotificationBuilder {

        String message, subject;
        protected User user;
        LogEntry logEntry;

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

        @Override
        public NotificationBuilder setLogEntry(LogEntry logEntry) {
            this.logEntry = logEntry;
            return this;
        }

        public Notification build() throws RuntimeException, SQLException {
            Notification notification = new Notification(message, user, subject, logEntry);
            DatabaseClient.getNotificationDao().create(notification);
            return notification;
        }
    }

    public static NotificationBuilder getInstance() {
        return new NotificationBuilderImpl();
    }

}
