package moderation.reactionRole;

import net.dv8tion.jda.api.JDA;
import servant.LoggingTask;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;

import static servant.Database.closeQuietly;

public class ReactionRole {
    private JDA jda;
    private long guildId;
    private long channelId;
    private long messageId;
    private String emoji;
    private long emoteGuildId;
    private long emoteId;

    public ReactionRole(JDA jda, long guildId, long channelId, long messageId, String emoji, long emoteGuildId, long emoteId) {
        this.jda = jda;
        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.emoji = emoji;
        this.emoteGuildId = emoteGuildId;
        this.emoteId = emoteId;
    }

    public boolean set(long roleId) {
        Connection connection = null;
        var wasSet = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (!hasEntry()) {
                var insert = connection.prepareStatement("INSERT INTO reaction_role (guild_id, channel_id, message_id, emoji, emote_guild_id, emote_id, role_id) VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channelId);
                insert.setLong(3, messageId);
                insert.setString(4, (emoji == null ? "" : emoji));
                insert.setLong(5, emoteGuildId);
                insert.setLong(6, emoteId);
                insert.setLong(7, roleId);
                insert.executeUpdate();
                wasSet = false;
            }
        } catch (SQLException e) {
            new LoggingTask(e, jda, "ReactionRole#set");
        } finally {
            closeQuietly(connection);
        }

        return wasSet;
    }

    public boolean unset() {
        Connection connection = null;
        var wasSet = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (hasEntry()) {
                var delete = connection.prepareStatement("DELETE FROM reaction_role WHERE guild_id=? AND channel_id=? AND message_id=? AND emoji=? AND emote_guild_id=? AND emote_id=?");
                delete.setLong(1, guildId);
                delete.setLong(2, channelId);
                delete.setLong(3, messageId);
                delete.setString(4, (emoji == null ? "" : emoji));
                delete.setLong(5, emoteGuildId);
                delete.setLong(6, emoteId);
                delete.executeUpdate();
                wasSet = false;
            }
        } catch (SQLException e) {
            new LoggingTask(e, jda, "ReactionRole#unset");
        } finally {
            closeQuietly(connection);
        }

        return wasSet;
    }

    public long getRoleId() {
        Connection connection = null;
        var roleId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT role_id FROM reaction_role WHERE guild_id=? AND channel_id=? AND message_id=? AND emoji=? AND emote_guild_id=? AND emote_id=?");
            select.setLong(1, guildId);
            select.setLong(2, channelId);
            select.setLong(3, messageId);
            select.setString(4, (emoji == null ? "" : emoji));
            select.setLong(5, emoteGuildId);
            select.setLong(6, emoteId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) roleId = resultSet.getLong("role_id");
        } catch (SQLException e) {
            new LoggingTask(e, jda, "ReactionRole#getRoleId");
        } finally {
            closeQuietly(connection);
        }

        return roleId;
    }

    public boolean hasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT role_id FROM reaction_role WHERE guild_id=? AND channel_id=? AND message_id=? AND emoji=? AND emote_guild_id=? AND emote_id=?");
            select.setLong(1, guildId);
            select.setLong(2, channelId);
            select.setLong(3, messageId);
            select.setString(4, (emoji == null ? "" : emoji));
            select.setLong(5, emoteGuildId);
            select.setLong(6, emoteId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new LoggingTask(e, jda, "ReactionRole#hasEntry");
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }
}
