package catfeeder.model.response.catfeeder.tag;

import catfeeder.model.Tag;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ReadCardResponse extends GeneralResponse {

    @XmlElement
    private Tag tag;
    @XmlElement
    private boolean isPresent;

    public ReadCardResponse() {}

    public ReadCardResponse(Tag tag, boolean isPresent) {
        this.tag = tag;
        this.isPresent = isPresent;
        success = true;
    }
}
