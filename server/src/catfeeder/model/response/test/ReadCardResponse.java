package catfeeder.model.response.test;

import catfeeder.feeder.response.CardInfo;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ReadCardResponse extends GeneralResponse {

    @XmlElement
    private CardInfo card;

    public ReadCardResponse() {}

    public ReadCardResponse(CardInfo card) {
        this.card = card;
        success = true;
    }
}
