package catfeeder.api;

import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.model.Notification;
import catfeeder.model.User;
import catfeeder.notifications.NotificationBuilderFactory;
import catfeeder.notifications.NotificationSender;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;

@Path("/test")
@Secured
public class TestEndpoint {

    @Context
    private SecurityContext context;

    @GET
    @Path("email")
    public boolean sendMeAnEmail() throws SQLException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        Notification notification = NotificationBuilderFactory.getInstance(Notification.NotificationType.EMAIL)
                .setMessageBody("Test email!")
                .setMessageSubject("Test subject!")
                .setRecipient(user)
                .build();

        return new NotificationSender(notification).sendNotification();
    }

}
