package catfeeder.notifications;

import catfeeder.db.DatabaseClient;
import catfeeder.model.Notification;
import catfeeder.model.NotificationRegistrations;
import catfeeder.util.SendNotificationPush;
import com.sendgrid.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ChromeNotificationSender implements NotificationSendStrategy {

    @Override
    public boolean sendNotification(Notification notification) {

        NotificationRegistrations query = new NotificationRegistrations();
        query.setUser(notification.getUser());
        try {
            List<NotificationRegistrations> registrations = DatabaseClient.getNotificationRegistrationsDao().queryForMatching(query);
            SendNotificationPush.sendNotificationPush(registrations);
            return true;
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
