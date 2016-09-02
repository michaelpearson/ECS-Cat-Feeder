package catfeeder.model.response.catfeeder.status;

import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class WeightResponse extends GeneralResponse {

    @XmlElement
    private int weight;

    public WeightResponse(int weight) {
        super(true);
        this.weight = weight;
    }
}
