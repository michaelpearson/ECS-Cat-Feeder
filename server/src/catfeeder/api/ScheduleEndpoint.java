package catfeeder.api;

import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.model.CatFeeder;
import catfeeder.model.FoodType;
import catfeeder.model.Schedule;
import catfeeder.model.User;
import catfeeder.model.response.GeneralResponse;
import catfeeder.model.response.schedule.ScheduleListResponse;
import catfeeder.model.response.schedule.ScheduleResponse;
import com.j256.ormlite.dao.Dao;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Secured
@Path("/schedule")
public class ScheduleEndpoint {

    private SimpleDateFormat htmlDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    @Context
    private SecurityContext context;

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public ScheduleListResponse getAllScheduledDeliveries(@QueryParam("month") int month, @QueryParam("year") int year) throws SQLException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        Collection<CatFeeder> feeders = user.getFeeders();
        List<Schedule> scheduleObjects = new ArrayList<>();
        for(CatFeeder f : feeders) {
            scheduleObjects.addAll(f.getScheduledDeliveries());
        }
        for(Schedule s : scheduleObjects) {
            s.populateDeliveries(month, year);
        }
        scheduleObjects = scheduleObjects.stream().filter(s -> s.getDerivedDeliveries().size() > 0).collect(Collectors.toList());
        return new ScheduleListResponse(scheduleObjects);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse deleteScheduledItem(@PathParam("id") int scheduleId) throws SQLException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        Dao<Schedule, Integer> schedulesDao = DatabaseClient.getScheduleDao();
        Schedule scheduleItem = schedulesDao.queryForId(scheduleId);
        if(scheduleItem == null || !user.doesUserOwnCatfeeder(scheduleItem.getFeeder())) {
            throw new NotFoundException("Schedule not found");
        }
        schedulesDao.delete(scheduleItem);
        return new GeneralResponse(true);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ScheduleResponse getScheduledDelivery(@PathParam("id") int id) throws SQLException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        Schedule s = DatabaseClient.getScheduleDao().queryForId(id);
        if(s == null || !user.doesUserOwnCatfeeder(s.getFeeder())) {
            throw new NotFoundException("Could not find schedule");
        }
        return new ScheduleResponse(s);
    }

    @POST
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ScheduleResponse updateDelivery(@PathParam("id") int scheduleId,
                                           @FormParam("feederId") int feederId,
                                           @FormParam("recurring") boolean recurring,
                                           @FormParam("daysOfWeek[]") List<String> daysOfWeekString,
                                           @FormParam("startDate") String startDate,
                                           @FormParam("endDate") String endDate,
                                           @FormParam("amountOfFood") int amount,
                                           @FormParam("foodType") int type,
                                           @FormParam("notes") String notes
    ) throws SQLException, ParseException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(feederId);
        FoodType foodType = DatabaseClient.getFoodTypeDao().queryForId(type);
        Dao<Schedule, Integer> scheduleDao = DatabaseClient.getScheduleDao();
        Schedule s = scheduleDao.queryForId(scheduleId);
        if(s == null || !user.doesUserOwnCatfeeder(s.getFeeder())) {
            throw new NotFoundException("Could not find schedule");
        }
        if(feeder == null || !user.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException("Feeder not found");
        }
        if(foodType == null) {
            throw new NotFoundException("Food type not found");
        }
        buildSchedule(s, feeder, recurring, foodType, amount, notes, startDate, endDate, daysOfWeekString);
        DatabaseClient.getScheduleDao().update(s);
        return new ScheduleResponse(s);
    }

    private ArrayList<Schedule.DayOfWeek> decodeDaysOfWeek(List<String> daysOfWeek) {
        List<Schedule.DayOfWeek> build = daysOfWeek.stream().map(Schedule.DayOfWeek::fromString).filter(d -> d != null).collect(Collectors.toList());
        return new ArrayList<>(build);
    }

    private Schedule buildSchedule(Schedule s,
                                   CatFeeder feeder,
                                   boolean recurring,
                                   FoodType foodType,
                                   int amount,
                                   String notes,
                                   String startDate,
                                   String endDate,
                                   List<String> daysOfWeekString) {

        if(s == null) {
            s = new Schedule();
        }
        s.setFeeder(feeder);
        s.setRecurring(recurring);
        s.setFoodType(foodType);
        s.setGramAmount(amount);
        s.setNotes(notes);
        s.setDaysOfWeek(decodeDaysOfWeek(daysOfWeekString));

        try {
            s.setStartDate(htmlDateFormat.parse(startDate));
            if(endDate != null && !endDate.equals("")) {
                System.out.printf("End date: %s\n", endDate);
                s.setEndDate(htmlDateFormat.parse(endDate));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new BadRequestException("Invalid date");
        }

        return s;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public ScheduleResponse scheduleNewDelivery(@FormParam("feederId") int feederId,
                                                   @FormParam("recurring") boolean recurring,
                                                   @FormParam("daysOfWeek[]") List<String> daysOfWeekString,
                                                   @FormParam("startDate") String startDate,
                                                   @FormParam("endDate") String endDate,
                                                   @FormParam("amountOfFood") int amount,
                                                   @FormParam("foodType") int type,
                                                   @FormParam("notes") String notes
                                                   ) throws SQLException, ParseException {
        User user = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(feederId);
        FoodType foodType = DatabaseClient.getFoodTypeDao().queryForId(type);


        if(feeder == null || !user.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException("Feeder not found");
        }
        if(foodType == null) {
            throw new NotFoundException("Food type not found");
        }
        Schedule s = buildSchedule(null, feeder, recurring, foodType, amount, notes, startDate, endDate, daysOfWeekString);
        DatabaseClient.getScheduleDao().create(s);
        return new ScheduleResponse(s);
    }
}
