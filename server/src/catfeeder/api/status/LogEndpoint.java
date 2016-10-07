package catfeeder.api.status;

import catfeeder.api.annotations.Secured;
import catfeeder.api.filters.LoggedInSecurityContext;
import catfeeder.db.DatabaseClient;
import catfeeder.model.CatFeeder;
import catfeeder.model.LogEntry;
import catfeeder.model.User;
import catfeeder.model.response.status.log.LogEntryResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Secured
@Path("/status/log/")
public class LogEndpoint {

    @Context
    private SecurityContext context;

    @Path("{id}/list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public LogEntryResponse getLogEntries(
            @QueryParam("maxItems") int maxItems,
            @QueryParam("offset") int offset,
            @PathParam("id") int hardwareId
    ) throws SQLException {
        User u = ((LoggedInSecurityContext.UserPrincipal)context.getUserPrincipal()).getUser();
        CatFeeder feeder = DatabaseClient.getFeederDao().queryForId(hardwareId);
        if(feeder == null || !u.doesUserOwnCatfeeder(feeder)) {
            throw new NotFoundException("Could not find cat feeder");
        }
        List<LogEntry> logEntries = feeder.getLogEntries()
                .stream()
                .skip(offset)
                .limit(maxItems)
                .collect(Collectors.toList());
        return new LogEntryResponse(logEntries, feeder.getLogEntries().size(), offset);
    }
}
