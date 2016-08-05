package catfeeder.db;

import catfeeder.Passwords;
import catfeeder.model.CatFeeder;
import catfeeder.model.FoodDelivery;
import catfeeder.model.Schedule;
import catfeeder.model.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.SQLException;
import java.util.Date;

public class DatabaseClient implements ServletContextListener {
    private static ConnectionSource connectionSource = null;

    static {
        try {
            connectionSource = new JdbcConnectionSource("jdbc:h2:mem:test");
            TableUtils.createTable(connectionSource, CatFeeder.class);
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Schedule.class);
            TableUtils.createTable(connectionSource, FoodDelivery.class);

            Dao<CatFeeder, Long> feederDao = DaoManager.createDao(connectionSource, CatFeeder.class);
            Dao<User, String> userDao = getUserDao();
            Dao<Schedule, Long> scheduleDao = getScheduleDao();

            CatFeeder cf = new CatFeeder();
            cf.setHardware_id(1);
            cf.setName("Test cat feeder");
            cf.setLastConnectionAt(null);
            feederDao.create(cf);

            for(int a = 0;a < 10;a++) {
                Schedule schedule = new Schedule();
                schedule.setRecurring(false);
                schedule.setGramAmount(100);
                schedule.setFirstDelivery(new Date());
                schedule.setFeeder(cf);
                scheduleDao.create(schedule);
            }

            userDao.create(createUser("test@test.com", "Test User", "password", cf));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static User createUser(String email, String name, String password, CatFeeder catFeeder) {
        User u = new User();
        u.setEmail(email);
        u.setName(name);
        u.setPassword(Passwords.getHash(password));
        u.setCatFeeder(catFeeder);
        return u;
    }

    public static Dao<User, String> getUserDao() throws SQLException {
        return DaoManager.createDao(connectionSource, User.class);
    }

    public static Dao<Schedule, Long> getScheduleDao() throws SQLException {
        return DaoManager.createDao(connectionSource, Schedule.class);
    }

    public static ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {}

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {}
}
