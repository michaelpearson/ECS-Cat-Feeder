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
            Dao<FoodType, Integer> foodTypeDao = getFoodTypeDao();

            if(!userDao.isTableExists()) {

                TableUtils.createTable(connectionSource, CatFeeder.class);
                TableUtils.createTable(connectionSource, User.class);
                TableUtils.createTable(connectionSource, Schedule.class);
                TableUtils.createTable(connectionSource, ScheduledFoodDelivery.class);
                TableUtils.createTable(connectionSource, FoodDelivery.class);
                TableUtils.createTable(connectionSource, SessionToken.class);
                TableUtils.createTable(connectionSource, FoodType.class);
                TableUtils.createTable(connectionSource, LogEntry.class);

                User user = createUser("test@test.com", "Test User", "password");
                userDao.create(user);

                CatFeeder cf = new CatFeeder();
                //This is the id of the esp we have
                int feederId = 9240906;
                cf.setHardwareId(feederId);
                cf.setName("Test cat feeder");
                cf.setLastConnectionAt(null);
                cf.setOwner(user);
                feederDao.create(cf);

                FoodType type = new FoodType();
                type.setName("Food type 1");
                type.setCatfeeder(cf);
                type.setFoodIndex(0);
                foodTypeDao.create(type);

                FoodType type1 = new FoodType();
                type1.setName("Food type 2");
                type1.setCatfeeder(cf);
                type1.setFoodIndex(1);
                foodTypeDao.create(type1);

                cf = feederDao.queryForId(feederId);

                for (int a = 0; a < 10; a++) {
                    Schedule schedule = new Schedule();
                    schedule.setRecurring(false);
                    schedule.setGramAmount(5 * a);
                    schedule.setStartDate(new Date());
                    schedule.setFeeder(cf);
                    schedule.setFoodType(a % 2 == 0 ? type : type1);
                    schedule.setNotes("Some note " + a);
                    scheduleDao.create(schedule);
                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static User createUser(String email, String name, String password) {
        User u = new User();
        u.setEmail(email);
        u.setName(name);
        u.setPassword(Passwords.getHash(password));
        return u;
    }

    public static Dao<User, String> getUserDao() throws SQLException {
        return DaoManager.createDao(connectionSource, User.class);
    }

    public static Dao<FoodType, Integer> getFoodTypeDao() throws SQLException {
        return DaoManager.createDao(connectionSource, FoodType.class);
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

    public static Dao<LogEntry, Integer> getLogEntryDao() throws SQLException {
        return DaoManager.createDao(connectionSource, LogEntry.class);
    }

    public static Dao<ScheduledFoodDelivery, Integer> getScheduledFoodDeliveryDao() throws SQLException {
        return DaoManager.createDao(connectionSource, ScheduledFoodDelivery.class);
    }

    public static Dao<FoodDelivery, Integer> getFoodDeliveryDao() throws SQLException {
        return DaoManager.createDao(connectionSource, FoodDelivery.class);
    }
}
