package catfeeder.mappers;

import catfeeder.db.DatabaseClient;
import catfeeder.model.Tag;
import catfeeder.model.CardInfo;
import catfeeder.model.response.catfeeder.tag.ReadCardResponse;
import catfeeder.util.First;

import java.sql.SQLException;

public class CardInfoToTagResponseMapper {
    public static ReadCardResponse mapCardToTagResponse(CardInfo cardInfo) throws SQLException {
        Tag t = new Tag();
        t.setTagUID(cardInfo.getCardId());
        t = First.orElse(DatabaseClient.getTagDao().queryForMatching(t), t);
        return new ReadCardResponse(t, cardInfo.isPresent());
    }
}
