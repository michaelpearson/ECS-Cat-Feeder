package catfeeder.model.response;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GeneralResponse {
    @XmlElement
    protected boolean success;

    public GeneralResponse(boolean success) {
        this.success = success;
    }

    public GeneralResponse() {}
}
