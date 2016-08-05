package catfeeder.api;

import catfeeder.db.DatabaseClient;
import catfeeder.model.User;
import catfeeder.util.WriteErrorResponse;
import catfeeder.util.WriteJsonResponse;
import com.j256.ormlite.dao.Dao;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends javax.servlet.http.HttpServlet {
    public static final String KEY_SESSION_USER_ID = "user_id";
    public static final String KEY_SESSION_LOGGED_IN = "logged_in";

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        try {
            Dao<User, String> userDao = DatabaseClient.getUserDao();
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            User user = userDao.queryForId(email);
            if(user == null || !user.checkPassword(password)) {
                WriteErrorResponse.Write(response, "Invalid username or password");
                return;
            }
            HttpSession session = request.getSession();
            session.setAttribute(KEY_SESSION_LOGGED_IN, true);
            session.setAttribute(KEY_SESSION_USER_ID, user.getEmail());
            JSONObject responseData = new JSONObject();
            responseData.put("success", true);
            WriteJsonResponse.writeResponse(response, responseData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
