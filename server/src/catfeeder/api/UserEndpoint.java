package catfeeder.api;

import catfeeder.api.annotations.Insecure;
import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.model.CatFeeder;
import catfeeder.model.FeederUserConnection;
import catfeeder.model.NotificationRegistrations;
import catfeeder.model.User;
import catfeeder.model.response.GeneralResponse;
import catfeeder.model.response.user.UserResponse;
import catfeeder.util.Passwords;
import com.j256.ormlite.dao.Dao;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.List;

@Secured
@Path("/user")
public class UserEndpoint {

    @Context
    private SecurityContext context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserResponse getProfileInformation() {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        return new UserResponse(u);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/notifications/registerPush")
    public GeneralResponse saveRegistrationId(@FormParam("registrationId") String registrationId) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        Dao<NotificationRegistrations, String> dao = DatabaseClient.getNotificationRegistrationsDao();
        dao.createOrUpdate(new NotificationRegistrations(registrationId, u));
        return new GeneralResponse(true);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/notifications/savePreferences")
    public GeneralResponse saveNotificationPreferences(@FormParam("types[]") List<Integer> types) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        u.removeAllNotificationMethods();
        for(int type : types) {
            u.addNotificationMethod(User.NotificationType.fromId(type));
        }
        DatabaseClient.getUserDao().update(u);
        return new GeneralResponse(true);
    }



    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/information")
    public GeneralResponse updateProfile(@FormParam("password") String newPassword, @FormParam("name") String name) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        if(newPassword != null && !newPassword.equals("")) {
            u.setPassword(Passwords.getHash(newPassword));
        }
        if(name != null && !name.equals("")) {
            u.setName(name);
        }
        DatabaseClient.getUserDao().update(u);
        return new GeneralResponse(true);
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Insecure
    public GeneralResponse createUser(@FormParam("email") String emailAddress,
                                      @FormParam("name") String name,
                                      @FormParam("password") String password,
                                      @FormParam("feederId") int feederId) throws SQLException {

        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(feederId);
        if(feeder == null || feeder.getOwners().size() != 0) {
            if(LoggedInSecurityContext.class.isAssignableFrom(LoggedInSecurityContext.class)) {
                User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
                if(!u.doesUserOwnCatfeeder(feeder)) {
                    throw new NotAuthorizedException("Sorry, this cat feeder already has an owner");
                }
            } else {
                throw new NotAuthorizedException("Sorry, this cat feeder already has an owner");
            }
        }

        Dao<User, String> userDao = DatabaseClient.getUserDao();
        User user = userDao.queryForId(emailAddress);
        if(user != null) {
            return new GeneralResponse(false, "Email address in use");
        }

        user = new User();
        user.setEmail(emailAddress);
        user.setPassword(Passwords.getHash(password));
        user.setName(name);
        userDao.create(user);

        FeederUserConnection connection = new FeederUserConnection();
        connection.setFeeder(feeder);
        connection.setUser(user);
        DatabaseClient.getFeederUserConnectionDao().create(connection);
        return new UserResponse(user);
    }
}
