package catfeeder.api;

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
    public GeneralResponse updatePassword(@FormParam("password") String newPassword) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        u.setPassword(Passwords.getHash(newPassword));
        DatabaseClient.getUserDao().update(u);
        return new GeneralResponse(true);
    }
}
