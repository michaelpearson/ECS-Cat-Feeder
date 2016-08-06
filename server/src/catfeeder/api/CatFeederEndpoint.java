package catfeeder.api;

import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.feeder.CatFeederConnection;
import catfeeder.feeder.SocketManager;
import catfeeder.model.CatFeeder;
import catfeeder.model.User;
import catfeeder.model.response.GeneralResponse;
import catfeeder.model.response.catfeeder.CatfeederListResponse;

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
        List<CatFeeder> catFeederList = new ArrayList<>();
        catFeederList.add(u.getCatFeeder());
        return new CatfeederListResponse(catFeederList);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/deliverFood")
    public GeneralResponse deliverFood(@PathParam("id") int hardwareId, @FormParam("amount") int gramAmount, @FormParam("type") int foodType) throws SQLException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(hardwareId);
        if(feeder == null || !user.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException();
        }
        CatFeederConnection connection = SocketManager.getCatfeederConnection(user.getCatFeeder().getHardwareId());
        if(connection == null) {
            return new GeneralResponse(false);
        }
        connection.deliverFood(gramAmount, foodType);
        return new GeneralResponse(true);
    }
}