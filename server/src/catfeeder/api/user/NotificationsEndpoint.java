package catfeeder.api.user;

import catfeeder.api.annotations.Insecure;
import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.model.Notification;
import catfeeder.model.NotificationRegistrations;
import catfeeder.model.User;
import catfeeder.model.response.GeneralResponse;
import catfeeder.model.response.notifications.NotificationListResponse;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.List;

@Secured
@Path("/user/notifications")
public class NotificationsEndpoint {

    @Context
    private SecurityContext context;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/registerPush")
    public GeneralResponse saveRegistrationId(@FormParam("registrationId") String registrationId) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        Dao<NotificationRegistrations, String> dao = DatabaseClient.getNotificationRegistrationsDao();
        dao.createOrUpdate(new NotificationRegistrations(registrationId, u));
        return new GeneralResponse(true);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/savePreferences")
    public GeneralResponse saveNotificationPreferences(@FormParam("types[]") List<Integer> types) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        u.removeAllNotificationMethods();
        for(int type : types) {
            u.addNotificationMethod(User.NotificationType.fromId(type));
        }
        DatabaseClient.getUserDao().update(u);
        return new GeneralResponse(true);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Insecure
    public GeneralResponse getUnseenNotifications(@QueryParam("registration") String registrationId) throws SQLException {
        Dao<NotificationRegistrations, String> registrationsDao = DatabaseClient.getNotificationRegistrationsDao();
        Dao<Notification, Integer> notificationDao = DatabaseClient.getNotificationDao();
        NotificationRegistrations registration = registrationsDao.queryForId(registrationId);
        if(registration == null) {
            throw new NotFoundException();
        }
        QueryBuilder<Notification, Integer> queryBuilder = notificationDao.queryBuilder();
        queryBuilder.where().eq("user_id", registration.getUser());
        queryBuilder.limit(1L);
        queryBuilder.orderBy("date", false);
        List<Notification> unseenNotifications = notificationDao.query(queryBuilder.prepare());

        for(Notification n : unseenNotifications) {
            n.setSeen();
            notificationDao.update(n);
        }

        return new NotificationListResponse(unseenNotifications);
    }





}
