// Author: Tancred423 (https://github.com/Tancred423)
package utilities;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static servant.Database.closeQuietly;

public class EmoteUtil {
    public static Emote getEmote(JDA jda, String name) {
        Connection connection = null;
        Emote emote = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT emote_guild_id, emote_id " +
                            "FROM const_emotes " +
                            "WHERE name=?");
            select.setString(1, name);
            var resultSet = select.executeQuery();

            if (resultSet.next()) {
                var sm = jda.getShardManager();
                if (sm != null) {
                    var thisGuild = sm.getGuildById(resultSet.getLong("emote_guild_id"));
                    if (thisGuild != null) emote = thisGuild.getEmoteById(resultSet.getLong("emote_id"));
                }
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "EmoteUtil#getEmote"));
        } finally {
            closeQuietly(connection);
        }

        return emote;
    }

    public static String getEmoji(JDA jda, String name) {
        Connection connection = null;
        String emoji = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement(
                    "SELECT emoji " +
                            "FROM const_emojis " +
                            "WHERE name=?");
            select.setString(1, name);
            var resultSet = select.executeQuery();
            if (resultSet.next()) emoji = resultSet.getString("emoji");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "EmoteUtil#getEmoji"));
        } finally {
            closeQuietly(connection);
        }

        return emoji;
    }
}
