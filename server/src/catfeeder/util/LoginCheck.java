package catfeeder.util;

import catfeeder.api.LoginServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class LoginCheck {
    public static boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null) {
            return false;
        }
        Boolean loggedIn = (Boolean)session.getAttribute(LoginServlet.KEY_SESSION_LOGGED_IN);
        return loggedIn != null && loggedIn;
    }
}
