package servant;

import config.ConfigFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection getConnection() throws SQLException {
        ConfigFile config = Servant.config;
        return DriverManager.getConnection("jdbc:mysql://" + config.getDatabaseUrl() + "?useUnicode=true&serverTimezone=UTC", config.getDatabaseUsername(), config.getDatabasePassword());
    }
}
