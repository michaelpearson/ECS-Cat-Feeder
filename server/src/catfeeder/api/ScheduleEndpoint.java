package catfeeder.api;

import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.model.CatFeeder;
import catfeeder.model.Schedule;
import catfeeder.model.User;
import catfeeder.model.response.GeneralResponse;
import catfeeder.model.response.ScheduleListResponse;
import catfeeder.model.response.shcedule.NewScheduleResponse;
import com.j256.ormlite.dao.Dao;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Secured
@Path("/schedule")
public class ScheduleEndpoint {

    @Context
    private SecurityContext context;

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public ScheduleListResponse getAllScheduledDeliveries(@QueryParam("month") int month, @QueryParam("year") int year) throws SQLException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        List<Schedule> scheduleObjects = new ArrayList<>(user.getCatFeeder().getScheduledDeliveries());
        for(Schedule s : scheduleObjects) {
            s.populateDeliveries(month, year);
        }
        scheduleObjects = scheduleObjects.stream().filter(s -> s.getDerivedDeliveries().size() > 0).collect(Collectors.toList());
        return new ScheduleListResponse(scheduleObjects);
    }

    @DELETE
    @Path("/{id}")
    public GeneralResponse deleteScheduledItem(@PathParam("id") int scheduleId) throws SQLException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        Dao<Schedule, Integer> schedulesDao = DatabaseClient.getScheduleDao();
        Schedule scheduleItem = schedulesDao.queryForId(scheduleId);
        if(scheduleItem == null || !user.doesUserOwnCatfeeder(scheduleItem.getFeeder())) {
            throw new NotFoundException("Schedule not found");
        }
        if(scheduleItem.isRecurring()) {
            throw new NotImplementedException();
        } else {
            schedulesDao.delete(scheduleItem);
            return new GeneralResponse(true);
        }
    }

    @POST
    public NewScheduleResponse scheduleNewDelivery(@FormParam("feederId") int feederId,
                                                   @FormParam("gramAmount") int amount,
                                                   @FormParam("foodIndex") int type,
                                                   @FormParam("date") String date,
                                                   @FormParam("recurring") boolean recurring) throws SQLException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(feederId);

        if(feeder == null || !user.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException("Feeder not found");
        }
        if(recurring) {
            throw new NotImplementedException();
        } else {
            Schedule s = new Schedule();
            s.setFeeder(feeder);
            s.setRecurring(false);
            s.setFirstDelivery(new Date(date));
            s.setGramAmount(amount);
            s.setFoodIndex(type);

            DatabaseClient.getScheduleDao().create(s);

            return new NewScheduleResponse(s);
        }
    }
}
