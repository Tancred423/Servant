// Author: Tancred423 (https://github.com/Tancred423)
package useful.giveaway;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;

import static utilities.DatabaseConn.closeQuietly;

public class GiveawayListener extends ListenerAdapter {
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        var guild = event.getGuild();
        var user = event.getGuild().getOwner().getUser();

        if (isGiveaway(guild.getIdLong(), event.getChannel().getIdLong(), event.getMessageIdLong(), guild, user))
            Giveaway.deleteGiveawayFromDb(event.getGuild().getIdLong(), event.getChannel().getIdLong(), event.getMessageIdLong(), event.getGuild(), event.getGuild().getOwner().getUser());
    }

    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        var guild = event.getGuild();
        var user = event.getGuild().getOwner().getUser();

        if (isGiveaway(guild.getIdLong(), event.getChannel().getIdLong(), guild, user))
            Giveaway.deleteGiveawayFromDb(event.getGuild().getIdLong(), event.getChannel().getIdLong(), event.getGuild(), event.getGuild().getOwner().getUser());
    }

    public void onGuildLeave(GuildLeaveEvent event) {
        var guild = event.getGuild();
        var user = event.getGuild().getOwner().getUser();

        if (isGiveaway(guild.getIdLong(), guild, user))
            Giveaway.deleteGiveawayFromDb(event.getGuild().getIdLong(), event.getGuild(), event.getGuild().getOwner().getUser());
    }

    private boolean isGiveaway(long guildId, long channelId, long messageId, Guild guild, User user) {
        Connection connection = null;
        var isGiveaway = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement("SELECT * FROM giveawaylist WHERE guild_id=? AND channel_id=? AND message_id=?");
            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, channelId);
            preparedStatement.setLong(3, messageId);
            var resultSet = preparedStatement.executeQuery();
            isGiveaway = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "giveaway", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isGiveaway;
    }

    private boolean isGiveaway(long guildId, long channelId, Guild guild, User user) {
        Connection connection = null;
        var isGiveaway = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement("SELECT * FROM giveawaylist WHERE guild_id=? AND channel_id=?");
            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, channelId);
            var resultSet = preparedStatement.executeQuery();
            isGiveaway = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "giveaway", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isGiveaway;
    }

    private boolean isGiveaway(long guildId, Guild guild, User user) {
        Connection connection = null;
        var isGiveaway = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement("SELECT * FROM giveawaylist WHERE guild_id=?");
            preparedStatement.setLong(1, guildId);
            var resultSet = preparedStatement.executeQuery();
            isGiveaway = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "giveaway", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isGiveaway;
    }
}
