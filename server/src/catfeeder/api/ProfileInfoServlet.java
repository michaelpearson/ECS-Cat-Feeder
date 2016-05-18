package catfeeder.api;

import catfeeder.db.DatabaseClient;
import catfeeder.util.LoginCheck;
import catfeeder.util.WriteErrorResponse;
import catfeeder.util.WriteJsonResponse;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!LoginCheck.isLoggedIn(req)) {
            WriteErrorResponse.Write(resp, "You must be logged in");
            return;
        }
        try {
            int userId = (int)req.getSession().getAttribute(LoginServlet.KEY_SESSION_USER_ID);
            PreparedStatement statement = DatabaseClient.getConnection().prepareStatement("SELECT * FROM USERS WHERE id = ?");
            statement.setInt(1, userId);
            ResultSet result = statement.executeQuery();
            if(!result.next()) {
                throw new RuntimeException("User not found");
            }
            JSONObject responseData = new JSONObject();
            String email = result.getString("email");
            String name = result.getString("name");
            responseData.put("email", email);
            responseData.put("name", name);
            responseData.put("success", true);
            WriteJsonResponse.writeResponse(resp, responseData);
        } catch (SQLException | RuntimeException e) {
            WriteErrorResponse.Write(resp, "Error communicating with database; " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!LoginCheck.isLoggedIn(req)) {
            WriteErrorResponse.Write(resp, "You must be logged in");
            return;
        }
        try {
            int userId = (int)req.getSession().getAttribute(LoginServlet.KEY_SESSION_USER_ID);
            PreparedStatement statement = DatabaseClient.getConnection().prepareStatement("UPDATE USERS set email = ?, name = ? WHERE id = ?;");
            statement.setString(1, req.getParameter("email"));
            statement.setString(2, req.getParameter("name"));
            statement.setInt(3, userId);
            statement.executeUpdate();

            JSONObject responseData = new JSONObject();
            responseData.put("success", true);
            WriteJsonResponse.writeResponse(resp, responseData);
        } catch (SQLException | RuntimeException e) {
            WriteErrorResponse.Write(resp, "Error communicating with database; " + e.getMessage());
        }
    }
}
