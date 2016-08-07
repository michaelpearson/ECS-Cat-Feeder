package catfeeder.db;

import catfeeder.Passwords;
import catfeeder.model.*;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Date;

public class DatabaseClient {
    private static ConnectionSource connectionSource = null;

    static {
        try {
            String connectionString = System.getProperty("db");
            if(connectionString == null) {
                //connectionSource = new JdbcConnectionSource("jdbc:h2:mem:test");
                connectionSource = new JdbcConnectionSource("jdbc:h2:~/.cat-feeder-db", "sa", "sa");
            } else {
                connectionSource = new JdbcConnectionSource(connectionString, System.getenv("DB_USERNAME"), System.getenv("DB_PASSWORD"));
            }

            Dao<User, String> userDao = getUserDao();
            Dao<CatFeeder, Integer> feederDao = getFeederDao();
            Dao<Schedule, Integer> scheduleDao = getScheduleDao();

            if(!userDao.isTableExists()) {

                TableUtils.createTable(connectionSource, CatFeeder.class);
                TableUtils.createTable(connectionSource, User.class);
                TableUtils.createTable(connectionSource, Schedule.class);
                TableUtils.createTable(connectionSource, FoodDelivery.class);
                TableUtils.createTable(connectionSource, SessionToken.class);

                CatFeeder cf = new CatFeeder();
                cf.setHardwareId(1);
                cf.setName("Test cat feeder");
                cf.setLastConnectionAt(null);
                feederDao.create(cf);

                for (int a = 0; a < 10; a++) {
                    Schedule schedule = new Schedule();
                    schedule.setRecurring(false);
                    schedule.setGramAmount(100);
                    schedule.setFirstDelivery(new Date());
                    schedule.setFeeder(cf);
                    scheduleDao.create(schedule);
                }

                userDao.create(createUser("test@test.com", "Test User", "password", cf));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static User createUser(String email, String name, String password, CatFeeder catFeeder) {
        User u = new User();
        u.setEmail(email);
        u.setName(name);
        u.setPassword(Passwords.getHash(password));
        return u;
    }

    public static Dao<User, String> getUserDao() throws SQLException {
        return DaoManager.createDao(connectionSource, User.class);
    }

    public static Dao<CatFeeder, Integer> getFeederDao() throws SQLException {
        return DaoManager.createDao(connectionSource, CatFeeder.class);
    }

    public static Dao<Schedule, Integer> getScheduleDao() throws SQLException {
        return DaoManager.createDao(connectionSource, Schedule.class);
    }

    public static Dao<SessionToken, String> getSessionTokenDao() throws SQLException {
        return DaoManager.createDao(connectionSource, SessionToken.class);
    }

    public static ConnectionSource getConnectionSource() {
        return connectionSource;
    }

}
