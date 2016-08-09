package catfeeder.model.response.food;

import catfeeder.model.FoodType;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlElement;

public class FoodResponse extends GeneralResponse {

    @XmlElement
    private FoodType foodType;


    public FoodResponse(FoodType foodType) {
        this.foodType = foodType;
        this.success = true;
    }

    public FoodResponse() {}
}
