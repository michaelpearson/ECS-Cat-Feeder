package catfeeder.api;

import catfeeder.db.DatabaseClient;
import catfeeder.feeder.CatFeederConnection;
import catfeeder.model.CatFeeder;
import catfeeder.model.response.test.ReadCardResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;

@Path("/test")
public class DeviceTestEndpoint {

    @Path("/readCard/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ReadCardResponse readCard(@PathParam("id") int id) throws SQLException {
        CatFeeder cf = DatabaseClient.getFeederDao().queryForId(id);
        CatFeederConnection connection = cf.getFeederConnection();
        if(connection == null) {
            return new ReadCardResponse();
        }
        return new ReadCardResponse(connection.queryLastCardId());
    }
}
