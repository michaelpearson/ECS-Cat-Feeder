package catfeeder.api;

import catfeeder.api.annotations.Insecure;
import catfeeder.model.CatFeeder;
import catfeeder.util.Passwords;
import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.model.User;
import catfeeder.model.response.GeneralResponse;
import catfeeder.model.response.user.UserResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;

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
            throw new NotFoundException();
        }

        User newUser = new User();
        newUser.setEmail(emailAddress);
        newUser.setPassword(Passwords.getHash(password));

        return null;



    }
}
