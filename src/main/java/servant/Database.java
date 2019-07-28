package servant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection getConnection() throws SQLException {
        var config = Servant.config;
        return DriverManager.getConnection("jdbc:mysql://" + config.getDatabaseUrl() + "?useUnicode=true&serverTimezone=UTC&characterEncoding=UTF-8", config.getDatabaseUsername(), config.getDatabasePassword());
    }
}
