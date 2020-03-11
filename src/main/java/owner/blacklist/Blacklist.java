// Author: Tancred423 (https://github.com/Tancred423)
package owner.blacklist;

import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static servant.Database.closeQuietly;

public class Blacklist {
    private JDA jda;

    public Blacklist(JDA jda) {
        this.jda = jda;
    }

    List<Long> getIds() {
        Connection connection = null;
        var blacklistedIds = new ArrayList<Long>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM blacklist");
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do blacklistedIds.add(resultSet.getLong("id")); while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Blacklist#getIds"));
        } finally {
            closeQuietly(connection);
        }

        return blacklistedIds;
    }

    public static boolean isBlacklisted(Guild guild, User user) {
        var isBlacklisted = false;
        if (guild != null && new Server(guild).isBlacklisted()) isBlacklisted = true;
        if (user != null && new Master(user).isBlacklisted()) isBlacklisted = true;
        return isBlacklisted;
    }
}
