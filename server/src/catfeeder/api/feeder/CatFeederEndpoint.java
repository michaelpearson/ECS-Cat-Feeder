package catfeeder.api.feeder;

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
import catfeeder.model.response.catfeeder.CatFeederResponse;
import catfeeder.model.response.catfeeder.CatfeederListResponse;
import catfeeder.model.response.catfeeder.tag.ReadCardResponse;
import catfeeder.util.First;
import com.j256.ormlite.dao.Dao;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;

@Secured
@Path("/feeders")
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
    @Path("/new")
    @Insecure
    public GeneralResponse createCatFeeder(@FormParam("hardwareId") int hardwareId,
                                           @FormParam("name") String name) throws SQLException {
        Dao<CatFeeder, Integer> feederDao = DatabaseClient.getFeederDao();
        CatFeeder newFeeder = new CatFeeder();
        newFeeder.setHardwareId(hardwareId);
        if(feederDao.queryForMatching(newFeeder).size() > 0) {
            return new GeneralResponse(false);
        }
        newFeeder.setName(name);
        DatabaseClient.getFeederDao().create(newFeeder);
        return new CatFeederResponse(newFeeder);
    }
}
