// Author: Tancred423 (https://github.com/Tancred423)
package commands.owner.blacklist;

import servant.MyGuild;
import servant.MyUser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static servant.Database.closeQuietly;

public class Blacklist {
    private final JDA jda;

    public Blacklist(JDA jda) {
        this.jda = jda;
    }

    List<Long> getIds() {
        Connection connection = null;
        var blacklistedIds = new ArrayList<Long>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT user_or_guild_id FROM global_blacklist");
            var resultSet = select.executeQuery();
            if (resultSet.next())
                do blacklistedIds.add(resultSet.getLong("user_or_guild_id")); while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Blacklist#getIds"));
        } finally {
            closeQuietly(connection);
        }

        return blacklistedIds;
    }

    public static boolean isBlacklisted(Guild guild, User user) {
        var isBlacklisted = false;
        if (guild != null && new MyGuild(guild).isBlacklisted()) isBlacklisted = true;
        if (user != null && new MyUser(user).isBlacklisted()) isBlacklisted = true;
        return isBlacklisted;
    }
}
