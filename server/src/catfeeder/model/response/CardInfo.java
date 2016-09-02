package catfeeder.model.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class CardInfo {
    @XmlElement
    private boolean isPresent;
    @XmlElement
    private long cardId;

    public CardInfo(boolean isPresent, long cardId) {
        this.isPresent = isPresent;
        this.cardId = cardId;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public long getCardId() {
        return cardId;
    }
}
