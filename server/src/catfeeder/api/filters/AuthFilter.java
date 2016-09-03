package catfeeder.api.filters;

import catfeeder.api.annotations.Insecure;
import catfeeder.api.annotations.Secured;
import catfeeder.db.DatabaseClient;
import catfeeder.model.SessionToken;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.sql.SQLException;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String authorizationHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            if(resourceInfo.getResourceMethod().getAnnotation(Insecure.class) != null) {
                return;
            }
            throw new NotAuthorizedException("Authorization header must be provided");
        }
        String token = authorizationHeader.substring("Bearer".length()).trim();
        try {
            SessionToken sessionToken = DatabaseClient.getSessionTokenDao().queryForId(token);
            if(sessionToken != null && sessionToken.isValid()) {
                containerRequestContext.setSecurityContext(new LoggedInSecurityContext(sessionToken.getUser()));
                return;
            }
        } catch(SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not connect to db");
        }

        if(resourceInfo.getResourceMethod().getAnnotation(Insecure.class) != null) {
            return;
        }
        throw new NotAuthorizedException("Authorization failed");
    }
}

