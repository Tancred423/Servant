// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConn {
    public static void closeQuietly(Connection connection) {
        try {
            if (connection != null) connection.close();
        } catch (SQLException ignored) { }
    }
}
