package catfeeder.api.feeder;

import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.feeder.CatFeederConnection;
import catfeeder.feeder.CatfeederSocketApplication;
import catfeeder.model.CatFeeder;
import catfeeder.model.FoodType;
import catfeeder.model.User;
import catfeeder.model.response.GeneralResponse;
import catfeeder.model.response.catfeeder.status.WeightResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;

@Secured
@Path("/feeder/{feederId}")
public class FeederFunctionsEndpoint {

    @Context
    private SecurityContext context;

    @GET
    @Path("/weight")
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

    @POST
    @Path("/deliverFood")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse deliverFood(@PathParam("feederId") int hardwareId,
                                       @FormParam("amount") int gramAmount,
                                       @FormParam("type") int foodTypeId) throws SQLException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(hardwareId);
        FoodType foodType = DatabaseClient.getFoodTypeDao().queryForId(foodTypeId);

        if(feeder == null || foodType == null || !user.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException();
        }

        boolean success = feeder.deliverFood(gramAmount, foodType);
        if(!success) {
            System.err.println("Cat feeder not connected");
            return new GeneralResponse(false);
        }
        return new GeneralResponse(true);
    }

    @PUT
    @Path("/tare")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse tareScale(@PathParam("feederId") int feederId) throws SQLException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder cf = DatabaseClient.getFeederDao().queryForId(feederId);
        if(cf == null || !user.doesUserOwnCatfeeder(cf)) {
            throw new NotFoundException();
        }
        CatFeederConnection connection = CatfeederSocketApplication.getCatfeederConnection(cf.getHardwareId());
        if(connection == null) {
            throw new ServiceUnavailableException();
        }
        connection.tare();
        return new GeneralResponse(true);
    }

}
