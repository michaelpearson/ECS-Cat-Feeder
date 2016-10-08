package catfeeder.model.response.notifications;

import catfeeder.model.Notification;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class NotificationListResponse extends GeneralResponse {

    @XmlElement
    private final List<Notification> unseenNotifications;

    public NotificationListResponse(List<Notification> unseenNotifications) {
        this.unseenNotifications = unseenNotifications;
        this.success = true;
    }
}
