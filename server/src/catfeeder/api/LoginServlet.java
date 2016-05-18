package catfeeder.api;

import catfeeder.db.DatabaseClient;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginServlet extends javax.servlet.http.HttpServlet {

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        response.setHeader("content-type", "application/json");
        JSONObject responseData = new JSONObject();
        Connection connection = DatabaseClient.getConnection();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            PreparedStatement statement = connection.prepareStatement("select * from users where email = ? and password = ?");
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();



        } catch (SQLException e) {
            e.printStackTrace();
        }



        if(email.equals("example@example.com") && password.equals("password")) {
            request.getSession().setAttribute("logged_in", true);
            request.getSession().setAttribute("user_id", 1);
            responseData.put("success", true);
        } else {
            responseData.put("success", false);
        }
        response.getWriter().write(responseData.toJSONString());
    }

}
