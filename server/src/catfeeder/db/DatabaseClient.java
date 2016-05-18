package catfeeder.db;

import catfeeder.Configuration;
import org.h2.jdbcx.JdbcDataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseClient {
    public static Connection getConnection() {
        String path = Configuration.getConfigurationString("database", "path");
        boolean needsSeed = !new File(path).exists();

        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:file:" + path);
        ds.setUser("sa");
        ds.setPassword("sa");
        try {
            Connection c = ds.getConnection();
            if(needsSeed) {
                seed(c);
            }
            return c;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get connection");
        }
    }
    private static void seed(Connection connection) throws SQLException {
        Statement s = connection.prepareStatement("create table users");
    }
}
