package catfeeder.api;

import catfeeder.model.response.status.HealthCheckResponse;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

@Path("/health")
public class HealthCheckEndpoint {

    @Context
    private Request context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {
        return Response
                .status(Response.Status.OK)
                .header("Access-Control-Allow-Origin", "http://192.168.1.1")
                .entity(new HealthCheckResponse())
                .build();
    }
}
