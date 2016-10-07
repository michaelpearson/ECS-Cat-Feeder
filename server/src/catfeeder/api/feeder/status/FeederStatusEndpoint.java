package catfeeder.api.feeder.status;

import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.mappers.FoodRemainingCollectionToResponse;
import catfeeder.model.CatFeeder;
import catfeeder.model.FoodRemainingLog;
import catfeeder.model.User;
import catfeeder.model.response.GeneralResponse;
import catfeeder.model.response.catfeeder.status.WeightGraphResponse;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.List;

@Path("/feeder/{feederId}/status")
@Secured
public class FeederStatusEndpoint {
    @Context
    private SecurityContext context;

    @Path("/weightGraph")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse getWeightGraphData(@PathParam("feederId") int feederId) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(feederId);
        if(feeder == null || !u.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException("Could not find the specified cat feeder");
        }

        Dao<FoodRemainingLog, Integer> dao = DatabaseClient.getFoodRemaningLogDao();
        QueryBuilder<FoodRemainingLog, Integer> queryBuilder = dao.queryBuilder();
        queryBuilder.setWhere(queryBuilder.where().eq("feeder_id", feeder));
        queryBuilder.limit(50L);
        queryBuilder.orderBy("entryDate", false);

        List<FoodRemainingLog> foodRemainingCollection = dao.query(queryBuilder.prepare());
        List<WeightGraphResponse.DataPoint> data = FoodRemainingCollectionToResponse.getResponse(foodRemainingCollection);
        return new WeightGraphResponse(data);
    }
}
