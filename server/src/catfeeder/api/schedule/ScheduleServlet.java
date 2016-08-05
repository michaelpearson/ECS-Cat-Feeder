package catfeeder.api.schedule;

import catfeeder.api.LoginServlet;
import catfeeder.db.DatabaseClient;
import catfeeder.model.CatFeeder;
import catfeeder.model.FoodDelivery;
import catfeeder.model.Schedule;
import catfeeder.model.User;
import catfeeder.util.LoginCheck;
import catfeeder.util.WriteErrorResponse;
import catfeeder.util.WriteJsonResponse;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!LoginCheck.isLoggedIn(req)) {
            WriteErrorResponse.Write(resp, "You must be logged in");
            return;
        }

        try {
            Dao<User, String> userDao = DatabaseClient.getUserDao();
            Dao<Schedule, Long> scheduleDao = DatabaseClient.getScheduleDao();

            User user = userDao.queryForId((String)req.getSession().getAttribute(LoginServlet.KEY_SESSION_USER_ID));
            if(user == null) {
                throw new RuntimeException("Could not find user");
            }
            Map<String, Object> query = new HashMap<>();
            query.put("feeder_id", user.getCatFeeder());
            List<Schedule> schedules = scheduleDao.queryForFieldValues(query);
            int year = Integer.valueOf(req.getParameter("year"));
            int month = Integer.valueOf(req.getParameter("month"));

            JSONArray allDeliveries = new JSONArray();
            for(Schedule s : schedules) {
                List<FoodDelivery> deliveries = s.getDeliveriesForMonth(year, month);
                for(FoodDelivery d : deliveries) {
                    JSONObject dItem = new JSONObject();
                    dItem.put("delivery_id", d.getId());
                    dItem.put("schedule_id", s.getId());
                    dItem.put("gramAmount", d.getGramAmount());
                    dItem.put("date", d.getDateTime().getTime());
                    allDeliveries.add(dItem);
                }
            }
            JSONObject responseData = new JSONObject();
            responseData.put("deliveries", allDeliveries);
            WriteJsonResponse.writeResponse(resp, responseData);
        } catch (SQLException | RuntimeException e) {
            WriteErrorResponse.Write(resp, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!LoginCheck.isLoggedIn(req)) {
            WriteErrorResponse.Write(resp, "You must be logged in");
            return;
        }

        try {
            boolean recurring = req.getParameter("recurring").equals("true");
            if(recurring) {
                throw new RuntimeException("Not implemented");
            } else {
                Date date = new Date(Long.valueOf(req.getParameter("date")));
                int gramAmount = Integer.valueOf(req.getParameter("gramAmount"));
                int foodIndex = Integer.valueOf(req.getParameter("foodIndex"));

                CatFeeder catFeeder = DatabaseClient.getUserDao().queryForId((String)req.getSession().getAttribute(LoginServlet.KEY_SESSION_USER_ID)).getCatFeeder();
                Schedule schedule = new Schedule();
                schedule.setRecurring(false);
                schedule.setFirstDelivery(date);
                schedule.setGramAmount(gramAmount);
                schedule.setFoodIndex(foodIndex);
                schedule.setFeeder(catFeeder);
                DatabaseClient.getScheduleDao().create(schedule);

                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("id", schedule.getId());
                WriteJsonResponse.writeResponse(resp, response);
            }
        } catch (SQLException | RuntimeException e) {
            WriteErrorResponse.Write(resp, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!LoginCheck.isLoggedIn(req)) {
            WriteErrorResponse.Write(resp, "You must be logged in");
            return;
        }

        try {
            Dao<Schedule, Long> scheduleDao = DatabaseClient.getScheduleDao();
            CatFeeder catFeeder = DatabaseClient.getUserDao().queryForId((String)req.getSession().getAttribute(LoginServlet.KEY_SESSION_USER_ID)).getCatFeeder();
            long id = Long.valueOf(req.getParameter("id"));
            Schedule s = scheduleDao.queryForId(id);
            if(s.getFeeder().getHardware_id() != catFeeder.getHardware_id()) {
                throw new RuntimeException("Sorry, you're not able to delete that");
            }
            scheduleDao.delete(s);
            JSONObject response = new JSONObject();
            response.put("success", true);
            WriteJsonResponse.writeResponse(resp, response);
        } catch(SQLException | RuntimeException e) {
            WriteErrorResponse.Write(resp, e.getMessage());
            e.printStackTrace();
        }
    }
}
