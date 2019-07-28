package moderation.reactionRoles;

import com.jagrosh.jdautilities.command.CommandEvent;
import servant.Database;
import servant.Log;

import java.sql.SQLException;

class ReactionRoleDatabase {
    static int setReactionRole(CommandEvent event, long guildId, long channelId, long messageId, String emoji, long emoteGuildId, long emoteId, long roleId) {
        try {
            var connection = Database.getConnection();
            if (hasEntry(guildId, channelId, messageId, emoji, emoteGuildId, emoteId)) {
                return 1;
            } else {
                // Insert
                var insert = connection.prepareStatement("INSERT INTO reaction_role (guild_id, channel_id, message_id, emoji, emote_guild_id, emote_id, role_id) VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channelId);
                insert.setLong(3, messageId);
                insert.setString(4, (emoji == null ? "" : emoji));
                insert.setLong(5, emoteGuildId);
                insert.setLong(6, emoteId);
                insert.setLong(7, roleId);
                insert.executeUpdate();
                connection.close();
                return 0;
            }
        } catch (SQLException e) {
            new Log(e, event, "reactionrole").sendLogSqlCommandEvent(true);
            return 2;
        }
    }

    static int unsetReactionRole(CommandEvent event, long guildId, long channelId, long messageId, String emoji, long emoteGuildId, long emoteId) {
        try {
            var connection = Database.getConnection();
            if (!hasEntry(guildId, channelId, messageId, emoji, emoteGuildId, emoteId)) {
                return 1;
            } else {
                // Delete
                var delete = connection.prepareStatement("DELETE FROM reaction_role WHERE guild_id=? AND channel_id=? AND message_id=? AND emoji=? AND emote_guild_id=? AND emote_id=?");
                delete.setLong(1, guildId);
                delete.setLong(2, channelId);
                delete.setLong(3, messageId);
                delete.setString(4, (emoji == null ? "" : emoji));
                delete.setLong(5, emoteGuildId);
                delete.setLong(6, emoteId);
                delete.executeUpdate();
                connection.close();
                return 0;
            }
        } catch (SQLException e) {
            new Log(e, event, "reactionrole").sendLogSqlCommandEvent(true);
            return 2;
        }
    }

    static long getRoleId(long guildId, long channelId, long messageId, String emoji, long emoteGuildId, long emoteId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT role_id FROM reaction_role WHERE guild_id=? AND channel_id=? AND message_id=? AND emoji=? AND emote_guild_id=? AND emote_id=?");
        select.setLong(1, guildId);
        select.setLong(2, channelId);
        select.setLong(3, messageId);
        select.setString(4, (emoji == null ? "" : emoji));
        select.setLong(5, emoteGuildId);
        select.setLong(6, emoteId);
        var resultSet = select.executeQuery();
        var roleId = 0L;
        if (resultSet.first()) roleId = resultSet.getLong("role_id");
        connection.close();
        return roleId;
    }

    static boolean hasEntry(long guildId, long channelId, long messageId, String emoji, long emoteGuildId, long emoteId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT role_id FROM reaction_role WHERE guild_id=? AND channel_id=? AND message_id=? AND emoji=? AND emote_guild_id=? AND emote_id=?");
        select.setLong(1, guildId);
        select.setLong(2, channelId);
        select.setLong(3, messageId);
        select.setString(4, (emoji == null ? "" : emoji));
        select.setLong(5, emoteGuildId);
        select.setLong(6, emoteId);
        var resultSet = select.executeQuery();
        var hasEntry = false;
        if (resultSet.first()) hasEntry = true;
        connection.close();
        return hasEntry;
    }
}
