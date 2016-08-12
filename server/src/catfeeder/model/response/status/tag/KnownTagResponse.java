package catfeeder.model.response.status.tag;

import catfeeder.model.Tag;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class KnownTagResponse extends GeneralResponse {
    @XmlElement
    private Tag tag;

    public KnownTagResponse() {}

    public KnownTagResponse(Tag tag) {
        this.tag = tag;
        this.success = true;
    }
}
