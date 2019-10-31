// Author: Tancred423 (https://github.com/Tancred423)
package owner.blacklist;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import servant.Log;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static utilities.DatabaseConn.closeQuietly;

public class Blacklist {
    public static boolean isBlacklisted(User user, Guild guild) {
        var isBlacklisted = false;
        if (isBlacklisted(user.getIdLong(), guild, user)) isBlacklisted = true;
        if (isBlacklisted(guild == null ? 0L : guild.getIdLong(), guild, user)) isBlacklisted = true;
        return isBlacklisted;
    }

    public static boolean isBlacklisted(long id, Guild guild, User user) {
        Connection connection = null;
        var isBlacklisted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM blacklist WHERE id=?");
            select.setLong(1, id);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isBlacklisted = true;
        } catch (SQLException e) {
            new Log(e, guild, user, "blacklist", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isBlacklisted;
    }

    static List<Long> getBlacklistedIds(Guild guild, User user) {
        Connection connection = null;
        var blacklistedIds = new ArrayList<Long>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM blacklist");
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do blacklistedIds.add(resultSet.getLong("id")); while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, guild, user, "blacklist", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return blacklistedIds;
    }

    static void setBlacklist(long id, Guild guild, User user) {
        Connection connection = null;

        try {
            if (!isBlacklisted(id, guild, user)) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO blacklist (id) VALUES (?)");
                insert.setLong(1, id);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "blacklist", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    static void unsetBlacklist(long id, Guild guild, User user) {
        Connection connection = null;

        try {
            if (isBlacklisted(id, guild, user)) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM blacklist WHERE id=?");
                delete.setLong(1, id);
                delete.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "blacklist", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }
}
