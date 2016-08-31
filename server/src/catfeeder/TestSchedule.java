package catfeeder;

import catfeeder.db.DatabaseClient;
import catfeeder.feeder.ScheduleManager;
import catfeeder.model.CatFeeder;
import catfeeder.model.Schedule;
import catfeeder.model.ScheduledItem;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Calendar;

public class TestSchedule {

    private static Calendar c = Calendar.getInstance();

    public static void main(String[] argv) throws SQLException {
        Dao<Schedule, Integer> scheduleDao = DatabaseClient.getScheduleDao();
        Dao<CatFeeder, Integer> catFeederDao = DatabaseClient.getFeederDao();

        ScheduleManager scheduleManager = new ScheduleManager(scheduleDao, catFeederDao.queryForAll().get(0), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));

        for(ScheduledItem si : scheduleManager) {
            System.out.println(si);
        }
    }
}
