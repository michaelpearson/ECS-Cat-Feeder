package catfeeder.model.response.catfeeder;

import catfeeder.model.CatFeeder;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class CatFeederResponse extends GeneralResponse {

    @XmlElement
    private CatFeeder feeder;

    public CatFeederResponse(CatFeeder feeder) {
        super(true);
        this.feeder = feeder;
    }
}
