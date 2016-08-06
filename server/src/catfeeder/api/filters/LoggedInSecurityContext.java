package catfeeder.api.filters;

import catfeeder.model.User;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class LoggedInSecurityContext implements SecurityContext {

    private final User user;

    public LoggedInSecurityContext(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Principal getUserPrincipal() {
        return new UserPrincipal(user);
    }

    @Override
    public boolean isUserInRole(String s) {
        return true;
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }

    public static class UserPrincipal implements Principal {

        private User u;

        UserPrincipal(User u) {
            this.u = u;
        }

        @Override
        public boolean equals(Object another) {
            return false;
        }

        @Override
        public String toString() {
            return null;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String getName() {
            return u.getName();
        }

        public User getUser() {
            return u;
        }
    }

}
