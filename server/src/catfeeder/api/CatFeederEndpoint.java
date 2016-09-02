package catfeeder.api;

import catfeeder.api.annotations.Insecure;
import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.feeder.CatFeederConnection;
import catfeeder.feeder.CatfeederSocketApplication;
import catfeeder.mappers.CardInfoToTagResponseMapper;
import catfeeder.model.CatFeeder;
import catfeeder.model.FoodType;
import catfeeder.model.Tag;
import catfeeder.model.User;
import catfeeder.model.CardInfo;
import catfeeder.model.response.GeneralResponse;
import catfeeder.model.response.catfeeder.CatfeederListResponse;
import catfeeder.model.response.catfeeder.UrlResponse;
import catfeeder.model.response.catfeeder.tag.ReadCardResponse;
import catfeeder.util.First;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;

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
    @Path("/{id}/tags/available")
    public ReadCardResponse getAvailableTag(@PathParam("id") int hardwareId) throws SQLException, InterruptedException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder query = new CatFeeder();
        query.setOwner(user);
        query.setHardwareId(hardwareId);
        CatFeeder cf = First.orNull(DatabaseClient.getFeederDao().queryForMatching(query));
        CardInfo info = cf.getLastCardInfo();
        if(info == null) {
            return new ReadCardResponse();
        }
        return CardInfoToTagResponseMapper.mapCardToTagResponse(info);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/tag/setTrusted")
    public GeneralResponse setTrustedTag(@PathParam("id") int feederId, @FormParam("tagId") int tagId) throws SQLException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        Tag t = DatabaseClient.getTagDao().queryForId(tagId);
        CatFeeder cf = DatabaseClient.getFeederDao().queryForId(feederId);
        if(t == null || cf == null || !user.isSame(t.getUser()) || !user.isSame(cf.getOwner())) {
            throw new NotFoundException();
        }
        cf.setTrustedTag(t);
        return new GeneralResponse(true);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/url")
    @Insecure
    public UrlResponse getConnectionDetails(@PathParam("id") int feederId) {
        return new UrlResponse(UrlResponse.HOST, UrlResponse.PORT);
    }

    @PUT
    @Path("{feederId}/tare")
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
