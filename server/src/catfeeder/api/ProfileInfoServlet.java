package catfeeder.api;

import catfeeder.Passwords;
import catfeeder.db.DatabaseClient;
import catfeeder.model.User;
import catfeeder.util.LoginCheck;
import catfeeder.util.WriteErrorResponse;
import catfeeder.util.WriteJsonResponse;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
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
            Dao<User, String> userDao = DatabaseClient.getUserDao();
            String email = (String)req.getSession().getAttribute(LoginServlet.KEY_SESSION_USER_ID);
            User user = userDao.queryForId(email);
            if(user == null) {
                throw new RuntimeException("Could not find user");
            }
            JSONObject responseData = new JSONObject();
            responseData.put("email", email);
            responseData.put("name", user.getName());
            responseData.put("success", true);
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
            Dao<User, String> userDao = DatabaseClient.getUserDao();
            User user = userDao.queryForId((String)req.getSession().getAttribute(LoginServlet.KEY_SESSION_USER_ID));
            if(user == null) {
                throw new RuntimeException("Could not find user");
            }
            user.setName(req.getParameter("name"));
            String updatedPassword = req.getParameter("password");
            if(updatedPassword != null && !updatedPassword.equals("")) {
                user.setPassword(Passwords.getHash(updatedPassword));
            }
            userDao.update(user);
            JSONObject responseData = new JSONObject();
            responseData.put("success", true);
            WriteJsonResponse.writeResponse(resp, responseData);
        } catch (SQLException | RuntimeException e) {
            WriteErrorResponse.Write(resp, "Error communicating with database; " + e.getMessage());
        }
    }
}
