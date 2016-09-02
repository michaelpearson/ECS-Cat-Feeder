package catfeeder.db;

import catfeeder.feeder.CatFeederConnection;
import catfeeder.feeder.CatfeederSocketApplication;
import catfeeder.model.Schedule;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class ScheduleDao extends EventHandlerDelegate<Schedule, Integer> {

    public ScheduleDao(Dao<Schedule, Integer> dao) { super(dao); }

    private void updateFeeder(Schedule data) throws SQLException {
        CatFeederConnection connection = CatfeederSocketApplication.getCatfeederConnection(data.getFeeder().getHardwareId());
        if(connection != null) {
            connection.updateAlarm();
        }
    }

    @Override
    public int delete(Schedule data) throws SQLException {
        int result = super.delete(data);
        updateFeeder(data);
        return result;
    }

    @Override
    public int update(Schedule data) throws SQLException {
        int result = super.update(data);
        updateFeeder(data);
        return result;
    }

    @Override
    public int create(Schedule data) throws SQLException {
        int result = super.create(data);
        updateFeeder(data);
        return result;
    }
}
