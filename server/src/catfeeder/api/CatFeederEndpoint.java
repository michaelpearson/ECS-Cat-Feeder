package catfeeder.api;

import catfeeder.api.annotations.Insecure;
import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.feeder.CatFeederConnection;
import catfeeder.feeder.SocketManager;
import catfeeder.model.CatFeeder;
import catfeeder.model.FoodType;
import catfeeder.model.User;
import catfeeder.model.response.GeneralResponse;
import catfeeder.model.response.catfeeder.CatfeederListResponse;
import catfeeder.model.response.catfeeder.UrlResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Secured
@Path("/feeder")
public class CatFeederEndpoint {

    @Context
    private SecurityContext context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public CatfeederListResponse getAllCatFeeders() {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        return new CatfeederListResponse(u.getFeeders());
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/deliverFood")
    public GeneralResponse deliverFood(@PathParam("id") int hardwareId, @FormParam("amount") int gramAmount, @FormParam("type") int foodTypeId) throws SQLException {
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/url")
    @Insecure
    public UrlResponse getConnectionDetails(@PathParam("id") int feederId) {
        return new UrlResponse(UrlResponse.HOST, UrlResponse.PORT);
    }
}
