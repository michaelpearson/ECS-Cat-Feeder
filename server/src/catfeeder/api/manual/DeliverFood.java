package catfeeder.api.manual;

import catfeeder.api.LoginServlet;
import catfeeder.db.DatabaseClient;
import catfeeder.feeder.CatFeederConnection;
import catfeeder.feeder.SocketManager;
import catfeeder.model.User;
import catfeeder.util.LoginCheck;
import catfeeder.util.WriteErrorResponse;
import catfeeder.util.WriteJsonResponse;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class DeliverFood extends javax.servlet.http.HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!LoginCheck.isLoggedIn(req)) {
            WriteErrorResponse.Write(resp, "You must be logged in");
            return;
        }
        try {
            Dao<User, String> userDao = DaoManager.createDao(DatabaseClient.getConnectionSource(), User.class);
            User user = userDao.queryForId((String)req.getSession().getAttribute(LoginServlet.KEY_SESSION_USER_ID));
            if(user == null) {
                throw new RuntimeException("Could not find user");
            }
            CatFeederConnection connection = SocketManager.getCatfeederConnection(user.getCatFeeder().getHardware_id());
            if(connection == null) {
                throw new RuntimeException("Feeder not connected");
            }
            connection.deliverFood(Integer.valueOf(req.getParameter("amount")), Integer.valueOf(req.getParameter("type")));

            JSONObject responseData = new JSONObject();
            responseData.put("success", true);
            WriteJsonResponse.writeResponse(resp, responseData);
        } catch (SQLException | RuntimeException e) {
            WriteErrorResponse.Write(resp, e.getMessage());
        }
    }
}
