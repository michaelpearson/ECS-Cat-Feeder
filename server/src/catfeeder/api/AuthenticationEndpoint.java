package catfeeder.api;

import catfeeder.db.DatabaseClient;
import catfeeder.model.SessionToken;
import catfeeder.model.User;
import catfeeder.model.response.auth.AuthenticationResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

@Path("/authentication")
public class AuthenticationEndpoint {

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticationResponse authenticate(@FormParam("email") String email, @FormParam("password") String password) throws SQLException {
        User user = DatabaseClient.getUserDao().queryForId(email);
        if(user == null || !user.checkPassword(password)) {
            return new AuthenticationResponse(false, null);
        }
        SessionToken token = new SessionToken();
        token.setDateIssued(new Date());
        token.setToken(generateToken());
        token.setUser(user);
        DatabaseClient.getSessionTokenDao().create(token);
        return new AuthenticationResponse(true, token.getToken());
    }

    private String generateToken() {
        Random random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
