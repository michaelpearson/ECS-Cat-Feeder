package catfeeder.api.feeder.status;

import catfeeder.api.annotations.Secured;
import catfeeder.model.response.GeneralResponse;
import catfeeder.model.response.catfeeder.status.WeightGraphResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Path("/feeder/{feederId}/status")
@Secured
public class FeederStatusEndpoint {

    private static Calendar calendar = Calendar.getInstance();

    @Path("/weightGraph")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse getWeightGraphData(@PathParam("feederId") int feederId) {

        calendar.setTime(new Date());
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - (1 * 60 * 60 * 24 * 7));

        List<WeightGraphResponse.DataPoint> data = new ArrayList<>();
        for(int a = 0;a < 50;a++) {
            data.add(new WeightGraphResponse.DataPoint((int)(Math.sin(a / 50.0 * Math.PI * 6) * 100), calendar.getTime()));
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + ((1 * 60 * 60 * 24 * 7) / 50));
        }
        return new WeightGraphResponse(data);
    }
}
