package catfeeder.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class FeederNotConnected extends WebApplicationException {

    public FeederNotConnected() {
        super(Response.status(503).entity("Feeder not connected").type("application/json").build());
    }

}
