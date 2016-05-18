package catfeeder.api;

import catfeeder.Passwords;
import catfeeder.db.DatabaseClient;
import catfeeder.util.WriteJsonResponse;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginServlet extends javax.servlet.http.HttpServlet {
    public static final String KEY_SESSION_USER_ID = "user_id";
    public static final String KEY_SESSION_LOGGED_IN = "logged_in";



    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setHeader("content-type", "application/json");
        JSONObject responseData = new JSONObject();
        Connection connection = DatabaseClient.getConnection();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            PreparedStatement statement = connection.prepareStatement("select password, id from users where email = ?");
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            if(!result.next()) {
                responseData.put("success", false);
                responseData.put("message", "Could not find user");
                WriteJsonResponse.writeResponse(response, responseData);
                return;
            }

            String hash = result.getString("password");
            if(Passwords.checkPassword(password, hash)) {
                responseData.put("success", true);
                HttpSession session = request.getSession();
                session.setAttribute(KEY_SESSION_LOGGED_IN, true);
                session.setAttribute(KEY_SESSION_USER_ID, result.getInt("id"));
                WriteJsonResponse.writeResponse(response, responseData);
            } else {
                responseData.put("success", false);
                responseData.put("message", "Invalid password");
                WriteJsonResponse.writeResponse(response, responseData);
            }
        } catch (SQLException e) {
            System.err.println("Could not execute query; " + e.getMessage());
            responseData.put("success", false);
            responseData.put("message", "Error executing query; " + e.getMessage());
            response.setStatus(503);
            WriteJsonResponse.writeResponse(response, responseData);
        }
    }
}
