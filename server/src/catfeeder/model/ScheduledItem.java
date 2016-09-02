package catfeeder.model;

import catfeeder.feeder.CatFeederConnection;

import java.util.Date;

public interface ScheduledItem {

    Date getDateTime();

    void execute(CatFeederConnection connection);

}
