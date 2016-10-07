package catfeeder.model.response.catfeeder.status;

import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
public class WeightGraphResponse extends GeneralResponse {

    @XmlAccessorType(XmlAccessType.NONE)
    public static class DataPoint {
        @XmlElement
        private int weight;
        @XmlElement
        private Date time;

        public DataPoint(int weight, Date time) {
            this.weight = weight;
            this.time = time;
        }

        public DataPoint() {}
    }

    public WeightGraphResponse(List<DataPoint> weights) {
        this.weights = weights;
        this.success = true;
    }

    @XmlElement
    private List<DataPoint> weights;

}
