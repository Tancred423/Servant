// Author: Tancred423 (https://github.com/Tancred423)
package owner.blacklist;

import servant.Database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Blacklist {
    public static boolean isBlacklisted(long id) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM blacklist");
        var resultSet = select.executeQuery();
        var isBlacklisted = false;
        if (resultSet.first())
            do if (resultSet.getLong("id") == id) isBlacklisted = true; while (resultSet.next());
        connection.close();
        return isBlacklisted;
    }

    static List<Long> getBlacklistedIds() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM blacklist");
        var resultSet = select.executeQuery();
        List<Long> blacklistedIds = new ArrayList<>();
        if (resultSet.first())
            do blacklistedIds.add(resultSet.getLong("id")); while (resultSet.next());
        connection.close();
        return blacklistedIds;
    }

    public static void setBlacklist(long id) throws SQLException {
        if (!isBlacklisted(id)) {
            var connection = Database.getConnection();
            var insert = connection.prepareStatement("INSERT INTO blacklist (id) VALUES (?)");
            insert.setLong(1, id);
            insert.executeUpdate();
            connection.close();
        }
    }

    static void unsetBlacklist(long id) throws SQLException {
        if (isBlacklisted(id)) {
            var connection = Database.getConnection();
            var delete = connection.prepareStatement("DELETE FROM blacklist WHERE id=?");
            delete.setLong(1, id);
            delete.executeUpdate();
            connection.close();
        }
    }
}
