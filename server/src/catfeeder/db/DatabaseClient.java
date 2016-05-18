package catfeeder.db;

import catfeeder.Configuration;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseClient {
    private static Server server;

    static {
        try {
            server = Server.createTcpServer().start();
            System.out.println(server.getURL());
        } catch (SQLException e) {
            throw new RuntimeException("Could not start database; " + e.getMessage());
        }
    }


    public static Connection getConnection() {
        String path = Configuration.getConfigurationString("database", "path");

        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:~/cat-feeder-db;AUTO_SERVER=TRUE");
        ds.setUser("sa");
        ds.setPassword("sa");
        try {
            Connection c = ds.getConnection();
            //seed(c);
            return c;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get connection");
        }
    }
    private static void seed(Connection connection) throws SQLException {
        System.out.println("Seeding database");
        ScriptRunner scriptRunner = new ScriptRunner(connection, true, true);
        try {
            scriptRunner.runScript(new FileReader(Configuration.getConfigurationString("database", "seed")));
        } catch (IOException e) {
            throw new RuntimeException("Could not seed database; " + e.getMessage());
        }
    }
}
