package catfeeder.model.response.catfeeder;

import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class UrlResponse extends GeneralResponse {
    @XmlElement
    private String host;
    @XmlElement
    private int port;

    public UrlResponse() {}

    public UrlResponse(String host, int port) {
        this.host = host;
        this.port = port;
        this.success = true;
    }
}
