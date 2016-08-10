package catfeeder.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Event_Log")
public class Log {

    @DatabaseField(generatedId = true)
    private int id;



    public enum EventType {
        FoodDelivery,
        Disconnection,
        Connection;

        @JsonValue
        public String getEventType() {
            return this.toString();
        }
    }

}
