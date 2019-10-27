// Author: Tancred423 (https://github.com/Tancred423)
package owner.blacklist;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import servant.Database;
import servant.Log;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Blacklist {
    public static boolean isBlacklisted(User user, Guild guild) {
        var isBlacklisted = false;
        try {
            if (isBlacklisted(user.getIdLong())) isBlacklisted = true;
            if (isBlacklisted(guild == null ? 0L : guild.getIdLong())) isBlacklisted = true;
        } catch (SQLException e) {
            new Log(e, guild, user, "blacklist", null).sendLog(false);
        }
        return isBlacklisted;
    }

    public static boolean isBlacklisted(long id) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM blacklist WHERE id=?");
        select.setLong(1, id);
        var resultSet = select.executeQuery();
        var isBlacklisted = false;
        if (resultSet.first()) isBlacklisted = true;
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

    static void setBlacklist(long id, CommandEvent event) {
        try {
            if (!isBlacklisted(id)) {
                var connection = Database.getConnection();
                var insert = connection.prepareStatement("INSERT INTO blacklist (id) VALUES (?)");
                insert.setLong(1, id);
                insert.executeUpdate();
                connection.close();
            }
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), "blacklist", event).sendLog(true);
        }
    }

    static void unsetBlacklist(long id, CommandEvent event) {
        try {
            if (isBlacklisted(id)) {
                var connection = Database.getConnection();
                var delete = connection.prepareStatement("DELETE FROM blacklist WHERE id=?");
                delete.setLong(1, id);
                delete.executeUpdate();
                connection.close();
            }
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), "blacklist", event).sendLog(true);
        }
    }
}
