package catfeeder.api.manual;

import catfeeder.api.LoginServlet;
import catfeeder.db.DatabaseClient;
import catfeeder.feeder.CatFeeder;
import catfeeder.feeder.SocketManager;
import catfeeder.util.LoginCheck;
import catfeeder.util.WriteErrorResponse;
import catfeeder.util.WriteJsonResponse;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeliverFood extends javax.servlet.http.HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!LoginCheck.isLoggedIn(req)) {
            WriteErrorResponse.Write(resp, "You must be logged in");
            return;
        }
        JSONObject responseData = new JSONObject();
        try {
            Connection c = DatabaseClient.getConnection();
            int userId = (int)req.getSession().getAttribute(LoginServlet.KEY_SESSION_USER_ID);
            PreparedStatement statement = DatabaseClient.getConnection().prepareStatement("SELECT feeder_id FROM USERS WHERE id = ?");
            statement.setInt(1, userId);
            ResultSet result = statement.executeQuery();
            if(!result.next()) {
                throw new RuntimeException("User not found");
            }
            int feederId = result.getInt("feeder_id");

            CatFeeder feeder = SocketManager.getCatfeederConnection(feederId);
            if(feeder == null) {
                throw new RuntimeException("Feeder not connected");
            }
            feeder.deliverFood(Integer.valueOf(req.getParameter("amount")), Integer.valueOf(req.getParameter("type")));
            responseData.put("success", true);
        } catch (SQLException | RuntimeException e) {
            responseData.put("success", false);
            e.printStackTrace();
        }

        WriteJsonResponse.writeResponse(resp, responseData);
    }
}
