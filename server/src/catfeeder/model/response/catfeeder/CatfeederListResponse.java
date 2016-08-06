package catfeeder.model.response.catfeeder;

import catfeeder.model.CatFeeder;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(name = "")
public class CatfeederListResponse extends GeneralResponse {
    @XmlElement
    private List<CatFeeder> catFeeders;

    public CatfeederListResponse(List<CatFeeder> catFeeders) {
        this.catFeeders = catFeeders;
        this.success = true;
    }

    public CatfeederListResponse() {}

    public List<CatFeeder> getCatFeeders() {
        return catFeeders;
    }

    public void setCatFeeders(List<CatFeeder> catFeeders) {
        this.catFeeders = catFeeders;
    }
}
