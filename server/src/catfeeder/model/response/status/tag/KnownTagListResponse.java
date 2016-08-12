package catfeeder.model.response.status.tag;

import catfeeder.model.Tag;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Collection;

@XmlAccessorType(XmlAccessType.NONE)
public class KnownTagListResponse extends GeneralResponse {
    @XmlElement
    Collection<Tag> tags;

    public KnownTagListResponse() {}

    public KnownTagListResponse(Collection<Tag> tags) {
        this.tags = tags;
        this.success = true;
    }
}
