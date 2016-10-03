package catfeeder.notifications;

import catfeeder.model.Notification;
import com.sendgrid.*;

import java.io.IOException;

public class EmailSender implements NotificationSendStrategy {

    @Override
    public boolean sendNotification(Notification notification) {
        Email from = new Email("catfeeder@catfeeder.herokuapp.com");
        Email to = new Email(notification.getUser().getEmail());
        Content content = new Content("text/plain", notification.getNotificationBody());
        Mail mail = new Mail(from, notification.getSubject(), to, content);

        SendGrid sendGrid = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();

        try {
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            Response response = sendGrid.api(request);
            System.out.println("Response status: " + response.statusCode);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
