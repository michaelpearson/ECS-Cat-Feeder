package catfeeder.api.feeder;

import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.feeder.CatFeederConnection;
import catfeeder.feeder.CatfeederSocketApplication;
import catfeeder.model.CatFeeder;
import catfeeder.model.User;
import catfeeder.model.response.catfeeder.status.WeightResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;

@Secured
@Path("/feeder/{feederId}/status")
public class FeederStatusEndpoint {

    @Context
    private SecurityContext context;

    @Path("/weight")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public WeightResponse getCurrentWeight(@PathParam("feederId") int feederId) throws SQLException, InterruptedException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(feederId);
        if(feeder == null || !u.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException("Could not find the specified cat feeder");
        }
        CatFeederConnection connection = CatfeederSocketApplication.getCatfeederConnection(feeder.getHardwareId());
        if(connection == null) {
            throw new ServiceUnavailableException("Sorry, the catfeeder is currently unavailable");
        }
        return new WeightResponse(connection.readWeight());
    }

}
