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
        NotificationBuilder setImage(String image);
        Notification build() throws RuntimeException, SQLException;
    }

    public static class NotificationBuilderImpl implements NotificationBuilder {
        private String message, subject;
        protected User user;
        private LogEntry logEntry;
        private String image;

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

        public NotificationBuilder setLogEntry(LogEntry logEntry) {
            this.logEntry = logEntry;
            return this;
        }

        @Override
        public NotificationBuilder setImage(String image) {
            this.image = image;
            return this;
        }


        public Notification build() throws RuntimeException, SQLException {
            Notification notification = new Notification(message, user, subject, image, logEntry);
            DatabaseClient.getNotificationDao().create(notification);
            return notification;
        }
    }

    public static NotificationBuilder getInstance() {
        return new NotificationBuilderImpl();
    }

}
