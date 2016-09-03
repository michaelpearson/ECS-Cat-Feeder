package catfeeder.model.response;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GeneralResponse {
    @XmlElement
    protected boolean success;

    @XmlElement
    private String message;

    public GeneralResponse(boolean success) {
        this.success = success;
    }

    public GeneralResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public GeneralResponse() {}
}
