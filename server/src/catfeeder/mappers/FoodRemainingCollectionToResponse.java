package catfeeder.mappers;

import catfeeder.model.FoodRemainingLog;
import catfeeder.model.response.catfeeder.status.WeightGraphResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FoodRemainingCollectionToResponse {

    public static List<WeightGraphResponse.DataPoint> getResponse(Collection<FoodRemainingLog> collection) {
        return collection.stream().map(f -> new WeightGraphResponse.DataPoint(f.getWeight(), f.getDate())).collect(Collectors.toCollection(ArrayList::new));
    }
}
