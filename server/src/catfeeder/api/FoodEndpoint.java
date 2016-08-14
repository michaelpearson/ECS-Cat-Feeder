package catfeeder.api;

import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.model.FoodType;
import catfeeder.model.User;
import catfeeder.model.response.food.FoodResponse;
import com.j256.ormlite.dao.Dao;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;

@Secured
@Path("/food")
public class FoodEndpoint {

    @Context
    private SecurityContext securityContext;

    @Path("/{id}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public FoodResponse updateFoodType(@PathParam("id") int foodTypeId,
                                          @FormParam("defaultGramAmount") int defaultGramAmount,
                                          @FormParam("name") String name) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)securityContext.getUserPrincipal()).getUser();
        Dao<FoodType, Integer> dao = DatabaseClient.getFoodTypeDao();
        FoodType ft = dao.queryForId(foodTypeId);
        if(ft == null || !u.doesUserOwnCatfeeder(ft.getCatfeeder())) {
            throw new NotFoundException("Could not find food type");
        }
        ft.setName(name);
        ft.setDefaultGramAmount(defaultGramAmount);
        dao.update(ft);
        return new FoodResponse(ft);
    }
}
