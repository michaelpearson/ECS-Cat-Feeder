package catfeeder.db;

import catfeeder.model.*;
import catfeeder.util.Passwords;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseClient {
    private static ConnectionSource connectionSource = null;

    static {
        try {
            String connectionString = System.getProperty("db");
            if(connectionString == null) {
                connectionSource = new JdbcConnectionSource("jdbc:h2:~/.cat-feeder-db", "sa", "sa");
            } else {
                connectionSource = new JdbcConnectionSource(connectionString, System.getenv("DB_USERNAME"), System.getenv("DB_PASSWORD"));
            }

            Dao<User, String> userDao = getUserDao();
            Dao<CatFeeder, Integer> feederDao = getFeederDao();
            Dao<FoodType, Integer> foodTypeDao = getFoodTypeDao();
            Dao<Tag, Integer> tagDao = getTagDao();

            if(!userDao.isTableExists()) {

                TableUtils.createTable(connectionSource, CatFeeder.class);
                TableUtils.createTable(connectionSource, User.class);
                TableUtils.createTable(connectionSource, Schedule.class);
                TableUtils.createTable(connectionSource, ScheduledFoodDelivery.class);
                TableUtils.createTable(connectionSource, FoodDelivery.class);
                TableUtils.createTable(connectionSource, SessionToken.class);
                TableUtils.createTable(connectionSource, FoodType.class);
                TableUtils.createTable(connectionSource, LogEntry.class);
                TableUtils.createTable(connectionSource, Tag.class);
                TableUtils.createTable(connectionSource, FeederUserConnection.class);
                TableUtils.createTable(connectionSource, Notification.class);
                TableUtils.createTable(connectionSource, FoodRemainingLog.class);
                TableUtils.createTable(connectionSource, NotificationRegistrations.class);

                CatFeeder cf = new CatFeeder();
                //This is the id of the esp we have
                int feederId = 9240906;
                cf.setHardwareId(feederId);
                cf.setName("Test cat feeder");
                cf.setLastConnectionAt(null);
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

                Tag tag = new Tag();
                tag.setTagName("Test Tag");
                tagDao.create(tag);


                User user = createUser("test@test.com", "Test User", "password");
                user.addNotificationMethod(User.NotificationType.EMAIL);
                userDao.create(user);


                FeederUserConnection feederUserConnection = new FeederUserConnection();
                feederUserConnection.setUser(user);
                feederUserConnection.setFeeder(cf);
                getFeederUserConnectionDao().create(feederUserConnection);

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
        return new ScheduleDao(DaoManager.createDao(connectionSource, Schedule.class));
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

    public static Dao<FeederUserConnection, Integer> getFeederUserConnectionDao() throws SQLException {
        return DaoManager.createDao(connectionSource, FeederUserConnection.class);
    }

    public static Dao<Tag, Integer> getTagDao() throws SQLException {
        return DaoManager.createDao(connectionSource, Tag.class);
    }

    public static Dao<Notification, Integer> getNotificationDao() throws SQLException {
        return DaoManager.createDao(connectionSource, Notification.class);
    }

    public static Dao<FoodRemainingLog, Integer> getFoodRemainingLogDao() throws SQLException {
        return DaoManager.createDao(connectionSource, FoodRemainingLog.class);
    }

    public static Dao<NotificationRegistrations, String> getNotificationRegistrationsDao() throws SQLException {
        return DaoManager.createDao(connectionSource, NotificationRegistrations.class);
    }

}
