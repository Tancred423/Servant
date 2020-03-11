// Author: Tancred423 (https://github.com/Tancred423)
package moderation.guild;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import servant.LoggingTask;
import servant.Servant;
import useful.remindme.RemindMe;

import java.sql.*;
import java.util.*;

import static servant.Database.closeQuietly;

public class Server {
    private Guild guild;
    private long guildId;
    private JDA jda;

    public Server(Guild guild) {
        this.guild = guild;
        this.guildId = guild.getIdLong();
        this.jda = guild.getJDA();
    }

    public Guild getGuild() { return guild; }
    public long getGuildId() { return guildId; }

    // Methods
    // Blacklist
    public boolean isBlacklisted() {
        Connection connection = null;
        var isBlacklisted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM blacklist WHERE id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isBlacklisted = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#isBlacklisted"));
        } finally {
            closeQuietly(connection);
        }

        return isBlacklisted;
    }

    public void setBlacklist() {
        Connection connection = null;

        try {
            if (!isBlacklisted()) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO blacklist (id) VALUES (?)");
                insert.setLong(1, guildId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setBlacklist"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetBlacklist() {
        Connection connection = null;

        try {
            if (isBlacklisted()) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM blacklist WHERE id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetBlacklist"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Poll
    public void purgePollsFromChannel(long channelId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM votes WHERE guild_id=? AND channel_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, channelId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#purgePollsFromChannel"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void purgePolls() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM votes WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#purgePolls"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Signup
    public void purgeSignupsFromChannel(long channelId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM signup WHERE guild_id=? AND channel_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, channelId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#purgeSignupsFromChannel"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void purgeSignups() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM signup WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#purgeSignups"));
        } finally {
            closeQuietly(connection);
        }
    }

    // BestOfImage
    private boolean bestOfImageHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            PreparedStatement select = connection.prepareStatement("SELECT * FROM best_of_image WHERE guild_id=?");
            select.setLong(1, guildId);
            ResultSet resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#bestOfImageHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public Emote getBestOfImageEmote() {
        Connection connection = null;
        Emote emote = null;

        var thisGuild = guild.getJDA().getGuildById(guildId);
        if (thisGuild == null) return null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM best_of_image WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                var guildId = resultSet.getLong("emote_guild_id");
                var emoteId = resultSet.getLong("emote_id");
                if (guildId != 0 && emoteId != 0) emote = thisGuild.getEmoteById(emoteId);
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getBestOfImageEmote"));
        } finally {
            closeQuietly(connection);
        }

        return emote;
    }

    public String getBestOfImageEmoji() {
        Connection connection = null;
        var emoji = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT emoji FROM best_of_image WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) emoji = resultSet.getString("emoji");
            if (emoji.isEmpty()) emoji = null;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getBestOfImageEmoji"));
        } finally {
            closeQuietly(connection);
        }

        return emoji;
    }

    public TextChannel getBestOfImageChannel() {
        Connection connection = null;
        TextChannel channel = null;

        var thisGuild = guild.getJDA().getGuildById(guildId);
        if (thisGuild == null) return null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT channel_id FROM best_of_image WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channel = thisGuild.getTextChannelById(resultSet.getLong("channel_id"));
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getBestOfImageChannel"));
        } finally {
            closeQuietly(connection);
        }

        return channel;
    }

    public int getBestOfImageNumber() {
        Connection connection = null;
        int number = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT number FROM best_of_image WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) number = resultSet.getInt("number");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getBestOfImageNumber"));
        } finally {
            closeQuietly(connection);
        }

        return number;
    }

    public int getBestOfImagePercentage() {
        Connection connection = null;
        int percentage = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT percentage FROM best_of_image WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) percentage = resultSet.getInt("percentage");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getBestOfImagePercentage"));
        } finally {
            closeQuietly(connection);
        }

        return percentage;
    }

    public void setBestOfImageChannel(long channelId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfImageHasEntry()) {
                var update = connection.prepareStatement("UPDATE best_of_image SET channel_id=? WHERE guild_id=?");
                update.setLong(1, channelId);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channelId);
                insert.setInt(3, 0);
                insert.setInt(4, 0);
                insert.setString(5, "");
                insert.setLong(6, 0);
                insert.setLong(7, 0);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setBestOfImageChannel"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void setBestOfImageEmote(long emoteGuildId, long emoteId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfImageHasEntry()) {
                var update = connection.prepareStatement("UPDATE best_of_image SET emoji=?, emote_guild_id=?, emote_id=? WHERE guild_id=?");
                update.setString(1, "");
                update.setLong(2, emoteGuildId);
                update.setLong(3, emoteId);
                update.setLong(4, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, 0);
                insert.setInt(3, 0);
                insert.setInt(4, 0);
                insert.setString(5, "");
                insert.setLong(6, emoteGuildId);
                insert.setLong(7, emoteId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setBestOfImageEmote"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void setBestOfImageEmoji(String emoji) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfImageHasEntry()) {
                var update = connection.prepareStatement("UPDATE best_of_image SET emoji=?, emote_guild_id=?, emote_id=? WHERE guild_id=?");
                update.setString(1, emoji);
                update.setLong(2, 0);
                update.setLong(3, 0);
                update.setLong(4, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, 0);
                insert.setInt(3, 0);
                insert.setInt(4, 0);
                insert.setString(5, emoji);
                insert.setLong(6, 0);
                insert.setLong(7, 0);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setBestOfImageEmoji"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void setBestOfImageNumber(int number) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfImageHasEntry()) {
                var update = connection.prepareStatement("UPDATE best_of_image SET number=? WHERE guild_id=?");
                update.setInt(1, number);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, 0);
                insert.setInt(3, number);
                insert.setInt(4, 0);
                insert.setString(5, "");
                insert.setLong(6, 0);
                insert.setLong(7, 0);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setBestOfImageNumber"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void setBestOfImagePercentage(int percentage) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bestOfImageHasEntry()) {
                var update = connection.prepareStatement("UPDATE best_of_image SET percentage=? WHERE guild_id=?");
                update.setInt(1, percentage);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, 0);
                insert.setInt(3, 0);
                insert.setInt(4, percentage);
                insert.setString(5, "");
                insert.setLong(6, 0);
                insert.setLong(7, 0);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setBestOfImagePercentage"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void addBestOfImageBlacklist(long messageId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (!bestOfImageIsBlacklisted(messageId)) {
                var insert = connection.prepareStatement("INSERT INTO best_of_image_bl (message_id) VALUES (?)");
                insert.setLong(1, messageId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#addBestOfImageBlacklist"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean bestOfImageIsBlacklisted(long messageId) {
        Connection connection = null;
        var isBlacklisted = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM best_of_image_bl WHERE message_id=?");
            select.setLong(1, messageId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isBlacklisted = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#bestOfImageIsBlacklisted"));
        } finally {
            closeQuietly(connection);
        }

        return isBlacklisted;
    }

    // Birthday
    public boolean wasGratulated(long userId) {
        Connection connection = null;
        var wasGratulated = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT was_gratulated FROM birthday_gratulation WHERE guild_id=? and user_id=?");
            select.setLong(1, guildId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) wasGratulated = resultSet.getBoolean("was_gratulated");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#wasGratulated"));
        } finally {
            closeQuietly(connection);
        }

        return wasGratulated;
    }

    private boolean birthdayGratulationHasEntry(long userId) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM birthday_gratulation WHERE guild_id=? and user_id=?");
            select.setLong(1, guildId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#birthdayGratulationHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void setGratulated(long userId) {
        Connection connection = null;

        try {
            if (!birthdayGratulationHasEntry(userId)) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO birthday_gratulation (guild_id,user_id,was_gratulated) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, userId);
                insert.setBoolean(3, true);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setGratulated"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetGratulated(long userId) {
        Connection connection = null;

        try {
            if (birthdayGratulationHasEntry(userId)) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM birthday_gratulation WHERE guild_id=? AND user_id=?");
                delete.setLong(1, guildId);
                delete.setLong(2, userId);
                delete.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetGratulated"));
        } finally {
            closeQuietly(connection);
        }
    }

    private void unsetGratulateds() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM birthday_gratulation WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetGratulateds"));
        } finally {
            closeQuietly(connection);
        }
    }

    public long getBirthdayMessageChannelId() {
        Connection connection = null;
        var channelId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT channel_id FROM birthday_messages WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channelId = resultSet.getLong("channel_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getBirthdayMessageChannelId"));
        } finally {
            closeQuietly(connection);
        }

        return channelId;
    }

    public long getBirthdayMessageMessageId() {
        Connection connection = null;
        var messageId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT message_id FROM birthday_messages WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) messageId = resultSet.getLong("message_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getBirthdayMessageMessageId"));
        } finally {
            closeQuietly(connection);
        }

        return messageId;
    }

    public long getBirthdayMessageAuthorId() {
        Connection connection = null;
        var messageId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT user_id FROM birthday_messages WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) messageId = resultSet.getLong("user_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getBirthdayMessageAuthorId"));
        } finally {
            closeQuietly(connection);
        }

        return messageId;
    }

    public boolean birthdayMessagesHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM birthday_messages WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#birthdayMessagesHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void setBirthdayMessage(long channelId, long messageId, long userId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (birthdayMessagesHasEntry()) {
                var update = connection.prepareStatement("UPDATE birthday_messages SET channel_id=?, message_id=?, user_id=? WHERE guild_id=?");
                update.setLong(1, channelId);
                update.setLong(2, messageId);
                update.setLong(3, userId);
                update.setLong(4, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO birthday_messages (guild_id,channel_id,message_id,user_id) VALUES (?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channelId);
                insert.setLong(3, messageId);
                insert.setLong(4, userId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setBirthdayMessage"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetBirthdayMessage() {
        Connection connection = null;

        try {
            if (birthdayMessagesHasEntry()) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM birthday_messages WHERE guild_id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetBirthdayMessage"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void purgeBirthday() {
        unsetBirthdayMessage();
        unsetBirthdayChannelId();
        unsetBirthdays();
        unsetGratulateds();
    }

    public Map<Long, String> getBirthdays() {
        Connection connection = null;
        var birthdays = new HashMap<Long, String>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM birthdays WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do birthdays.put(resultSet.getLong("user_id"), resultSet.getString("birthday"));
                while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getBirthdays"));
        } finally {
            closeQuietly(connection);
        }

        return birthdays;
    }

    private boolean birthdaysHasEntry(long userId) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT birthday FROM birthdays WHERE guild_id=? AND user_id=?");
            select.setLong(1, guildId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#birthdaysHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void setBirthday(long userId, String birthday) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (birthdaysHasEntry(userId)) {
                var update = connection.prepareStatement("UPDATE birthdays SET birthday=? WHERE guild_id=? AND user_id=?");
                update.setString(1, birthday);
                update.setLong(2, guildId);
                update.setLong(3, userId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO birthdays (guild_id,user_id,birthday) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, userId);
                insert.setString(3, birthday);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setBirthday"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetBirthday(long userId) {
        Connection connection = null;
        var wasUnset = false;

        try {
            if (birthdaysHasEntry(userId)) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM birthdays WHERE guild_id=? AND user_id=?");
                delete.setLong(1, guildId);
                delete.setLong(2, userId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetBirthday"));
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    private void unsetBirthdays() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM birthdays WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetBirthdays"));
        } finally {
            closeQuietly(connection);
        }
    }

    public long getBirthdayChannelId() {
        Connection connection = null;
        var birthdayChannelId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT birthday_channel_id FROM guild WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) birthdayChannelId = resultSet.getLong("birthday_channel_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getBirthdayChannelId"));
        } finally {
            closeQuietly(connection);
        }

        return birthdayChannelId;
    }

    public void setBirthdayChannelId(long birthdayChannelId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (guildHasEntry()) {
                var update = connection.prepareStatement("UPDATE guild SET birthday_channel_id=? WHERE guild_id=?");
                update.setLong(1, birthdayChannelId);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO guild (guild_id,prefix,offset,language,birthday_channel_id) VALUES (?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, Servant.config.getDefaultPrefix());
                insert.setString(3, "Z");
                insert.setString(4, Servant.config.getDefaultLanguage());
                insert.setLong(5, birthdayChannelId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setBirthdayChannelId"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetBirthdayChannelId() {
        setBirthdayChannelId(0L);
    }

    // Guild
    private boolean guildHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM guild WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#guildHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    // Level Role
    public List<Long> getLevelRolesForLevel(int level) {
        Connection connection = null;
        List<Long> roleId = new ArrayList<>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM level_role WHERE guild_id=? ORDER BY level DESC");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do if (level >= resultSet.getInt("level")) roleId.add(resultSet.getLong("role_id"));
                while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getLevelRolesForLevel"));
        } finally {
            closeQuietly(connection);
        }

        return roleId;
    }

    public List<Long> getLevelRole(int level) {
        Connection connection = null;
        List<Long> roleId = new ArrayList<>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT role_id FROM level_role WHERE guild_id=? AND level=?");
            select.setLong(1, guildId);
            select.setInt(2, level);
            var resultSet = select.executeQuery();
            if (resultSet.first()) do roleId.add(resultSet.getLong("role_id")); while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getLevelRole"));
        } finally {
            closeQuietly(connection);
        }

        return roleId;
    }

    public Map<Integer, Long> getLevelRoles() {
        Connection connection = null;
        Map<Integer, Long> levelRoles = new HashMap<>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM level_role WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do levelRoles.put(resultSet.getInt("level"), resultSet.getLong("role_id"));
                while(resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getLevelRoles"));
        } finally {
            closeQuietly(connection);
        }

        return levelRoles;
    }

    public boolean setLevelRole(int level, long roleId) {
        Connection connection = null;
        var wasSet = false;

        try {
            if (!getLevelRole(level).contains(roleId)) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO level_role (guild_id,level,role_id) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setInt(2, level);
                insert.setLong(3, roleId);
                insert.executeUpdate();
                wasSet = true;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setLevelRole"));
        } finally {
            closeQuietly(connection);
        }

        return wasSet;
    }

    public void unsetLevelRole(int level, long roleId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM level_role WHERE guild_id=? AND level=? AND role_id=?");
            delete.setLong(1, guildId);
            delete.setInt(2, level);
            delete.setLong(3, roleId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetLevelRole"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Language
    public String getLanguage() {
        Connection connection = null;
        String language = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT language FROM guild WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) language = resultSet.getString("language");
            if (language == null) language = Servant.config.getDefaultLanguage();
            else if (language.isEmpty()) language = Servant.config.getDefaultLanguage();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getLanguage"));
        } finally {
            closeQuietly(connection);
        }

        return language;
    }

    public void setLanguage(String language) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (guildHasEntry()) {
                var update = connection.prepareStatement("UPDATE guild SET language=? WHERE guild_id=?");
                update.setString(1, language);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO guild (guild_id,prefix,offset,language,birthday_channel_id) VALUES (?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, Servant.config.getDefaultPrefix());
                insert.setString(3, Servant.config.getDefaultOffset());
                insert.setString(4, language);
                insert.setLong(5, 0);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setLanguage"));
        } finally {
            closeQuietly(connection);
        }
    }

    void unsetLanguage() {
        setLanguage(Servant.config.getDefaultLanguage());
    }

    // Voice Lobby
    public List<Long> getVoiceLobbies() {
        Connection connection = null;
        var lobbies = new ArrayList<Long>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM lobby WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) do lobbies.add(resultSet.getLong("channel_id")); while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getVoiceLobbies"));
        } finally {
            closeQuietly(connection);
        }

        return lobbies;
    }

    // Prefix.
    public String getPrefix() {
        Connection connection = null;
        var prefix = Servant.config.getDefaultPrefix();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT prefix FROM guild WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) prefix = resultSet.getString("prefix");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getPrefix"));
        } finally {
            closeQuietly(connection);
        }

        return prefix;
    }


    public void setPrefix(String prefix) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (guildHasEntry()) {
                var update = connection.prepareStatement("UPDATE guild SET prefix=? WHERE guild_id=?");
                update.setString(1, prefix);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO guild (guild_id,prefix,offset,language,birthday_channel_id) VALUES (?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, prefix);
                insert.setString(3, Servant.config.getDefaultOffset());
                insert.setString(4, Servant.config.getDefaultLanguage());
                insert.setLong(5, 0L);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setPrefix"));
        } finally {
            closeQuietly(connection);
        }
    }

    void unsetPrefix() {
        setPrefix(Servant.config.getDefaultPrefix());
    }

    // Offset
    public String getOffset() {
        Connection connection = null;
        var offset = Servant.config.getDefaultOffset();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT offset FROM guild WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) offset = resultSet.getString("offset");
            offset = offset.equals("00:00") ? "Z" : offset;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getOffset"));
        } finally {
            closeQuietly(connection);
        }

        return offset;
    }

    public void setOffset(String offset) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (guildHasEntry()) {
                var update = connection.prepareStatement("UPDATE guild SET offset=? WHERE guild_id=?");
                update.setString(1, offset);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO guild (guild_id,prefix,offset,language,birthday_channel_id) VALUES (?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, Servant.config.getDefaultPrefix());
                insert.setString(3, offset);
                insert.setString(4, Servant.config.getDefaultLanguage());
                insert.setLong(5, 0L);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setOffset"));
        } finally {
            closeQuietly(connection);
        }
    }

    void unsetOffset() {
        setOffset(Servant.config.getDefaultOffset());
    }

    // Featurecount
    private boolean featureCountHasEntry(String key) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM feature_count WHERE id=? AND feature=?");
            select.setLong(1, guildId);
            select.setString(2, key);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#featureCountHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    private int getFeatureCount(String feature) {
        Connection connection = null;
        var featureCount = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT count FROM feature_count WHERE id=? AND feature=?");
            select.setLong(1, guildId);
            select.setString(2, feature.toLowerCase());
            var resultSet = select.executeQuery();
            if (resultSet.first()) featureCount = resultSet.getInt("count");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getFeatureCount"));
        } finally {
            closeQuietly(connection);
        }

        return featureCount;
    }

    public void incrementFeatureCount(String feature) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (featureCountHasEntry(feature)) {
                var count = getFeatureCount(feature);
                var update = connection.prepareStatement("UPDATE feature_count SET count=? WHERE id=? AND feature=?");
                update.setInt(1, count + 1);
                update.setLong(2, guildId);
                update.setString(3, feature.toLowerCase());
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO feature_count (id,feature,count) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, feature.toLowerCase());
                insert.setInt(3, 1);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#incrementFeatureCount"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Level
    public Map<Long, Integer> getLeaderboard() {
        Connection connection = null;
        var userExp = new LinkedHashMap<Long, Integer>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM user_exp WHERE guild_id=? ORDER BY exp DESC");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do userExp.put(resultSet.getLong("user_id"), resultSet.getInt("exp"));
                while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getLeaderboard"));
        } finally {
            closeQuietly(connection);
        }

        return userExp;
    }

    // Autorole
    public boolean hasAutorole() {
        Connection connection = null;
        var hasAutorole = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM autorole WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasAutorole = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#hasAutorole"));
        } finally {
            closeQuietly(connection);
        }

        return hasAutorole;
    }

    public Map.Entry<Role, Integer> getAutorole() {
        Connection connection = null;
        var role = new HashMap<Role, Integer>();

        var thisGuild = guild.getJDA().getGuildById(guildId);
        if (thisGuild == null) return null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM autorole WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                var roleId = resultSet.getLong("role_id");
                var delay = resultSet.getInt("delay");
                role.put(thisGuild.getRoleById(roleId), delay);
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getAutorole"));
        } finally {
            closeQuietly(connection);
        }

        if (role.isEmpty()) return null;
        else return role.entrySet().iterator().next();
    }

    private boolean autoroleHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM autorole WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#autoroleHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void setAutorole(long roleId, int delay) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (autoroleHasEntry()) {
                var update = connection.prepareStatement("UPDATE autorole SET role_id=?, delay=? WHERE guild_id=?");
                update.setLong(1, roleId);
                update.setInt(2, delay);
                update.setLong(3, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO autorole (guild_id,role_id,delay) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, roleId);
                insert.setInt(3, delay);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setAutorole"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetAutorole() {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (autoroleHasEntry()) {
                var delete = connection.prepareStatement("DELETE FROM autorole WHERE guild_id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetAutorole"));
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    // MediaOnlyChannel
    public boolean mediaOnlyChannelHasEntry(MessageChannel channel) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM mediaonlychannel WHERE guild_id=? AND channel_id=?");
            select.setLong(1, guildId);
            select.setLong(2, channel.getIdLong());
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#mediaOnlyChannelHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public boolean setMediaOnlyChannel(MessageChannel channel) {
        Connection connection = null;
        var wasSet = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (!mediaOnlyChannelHasEntry(channel)) {
                var insert = connection.prepareStatement("INSERT INTO mediaonlychannel (guild_id,channel_id) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channel.getIdLong());
                insert.executeUpdate();
                wasSet = true;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setMediaOnlyChannel"));
        } finally {
            closeQuietly(connection);
        }

        return wasSet;
    }

    public boolean unsetMediaOnlyChannel(MessageChannel channel) {
        Connection connection = null;
        var wasUnset = false;

        try {
            if (mediaOnlyChannelHasEntry(channel)) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM mediaonlychannel WHERE guild_id=? AND channel_id=?");
                delete.setLong(1, guildId);
                delete.setLong(2, channel.getIdLong());
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetMediaOnlyChannel"));
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    public void purgeMediaOnlyChannels() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM mediaonlychannel WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#purgeMediaOnlyChannels"));
        } finally {
            closeQuietly(connection);
        }
    }

    public List<MessageChannel> getMediaOnlyChannels() {
        Connection connection = null;
        var channels = new ArrayList<MessageChannel>();

        var thisGuild = guild.getJDA().getGuildById(guildId);
        if (thisGuild == null) return null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM mediaonlychannel WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do channels.add(thisGuild.getTextChannelById(resultSet.getLong("channel_id")));
                while (resultSet.next());
            }
            if (channels.isEmpty()) channels = null;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getMediaOnlyChannels"));
        } finally {
            closeQuietly(connection);
        }

        return channels;
    }

    // Toggle
    private boolean toggleHasEntry(String feature) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM toggle WHERE guild_id=? AND feature=?");
            select.setLong(1, guildId);
            select.setString(2, feature);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#toggleHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public boolean getToggleStatus(String feature) {
        Connection connection = null;
        var isEnabled = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT is_enabled FROM toggle WHERE guild_id=? AND feature=?");
            select.setLong(1, guildId);
            select.setString(2, feature);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isEnabled = resultSet.getBoolean("is_enabled");
            else isEnabled = Servant.toggle.get(feature);
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getToggleStatus"));
        } finally {
            closeQuietly(connection);
        }

        return isEnabled;
    }

    public void setToggleStatus(String feature, boolean status) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (toggleHasEntry(feature)) {
                var update = connection.prepareStatement("UPDATE toggle SET is_enabled=? WHERE guild_id=? AND feature=?");
                update.setBoolean(1, status);
                update.setLong(2, guildId);
                update.setString(3, feature);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO toggle (guild_id,feature,is_enabled) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, feature);
                insert.setBoolean(3, status);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setToggleStatus"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Join
    private boolean joinNotifierHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM join_notifier WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#joinNotifierHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public TextChannel getJoinNotifierChannel() {
        Connection connection = null;
        TextChannel channel = null;

        var thisGuild = guild.getJDA().getGuildById(guildId);
        if (thisGuild == null) return null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT channel_id FROM join_notifier WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channel = thisGuild.getTextChannelById(resultSet.getLong("channel_id"));
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getJoinNotifierChannel"));
        } finally {
            closeQuietly(connection);
        }

        return channel;
    }

    public void setJoinNotifierChannel(MessageChannel channel) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (joinNotifierHasEntry()) {
                var update = connection.prepareStatement("UPDATE join_notifier SET channel_id=? WHERE guild_id=?");
                update.setLong(1, channel.getIdLong());
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO join_notifier (guild_id,channel_id) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channel.getIdLong());
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setJoinNotifierChannel"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetJoinNotifierChannel() {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (joinNotifierHasEntry()) {
                var delete = connection.prepareStatement("DELETE FROM join_notifier WHERE guild_id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetJoinNotifierChannel"));
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    // Leave
    private boolean leaveNotifierHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM leave_notifier WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#leaveNotifierHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public TextChannel getLeaveNotifierChannel() {
        Connection connection = null;
        TextChannel channel = null;

        var thisGuild = guild.getJDA().getGuildById(guildId);
        if (thisGuild == null) return null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT channel_id FROM leave_notifier WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channel = thisGuild.getTextChannelById(resultSet.getLong("channel_id"));
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getLeaveNotifierChannel"));
        } finally {
            closeQuietly(connection);
        }

        return channel;
    }

    public void setLeaveNotifierChannel(MessageChannel channel) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (leaveNotifierHasEntry()) {
                var update = connection.prepareStatement("UPDATE leave_notifier SET channel_id=? WHERE guild_id=?");
                update.setLong(1, channel.getIdLong());
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO leave_notifier (guild_id,channel_id) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channel.getIdLong());
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setLeaveNotifierChannel"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetLeaveNotifierChannel() {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (leaveNotifierHasEntry()) {
                var delete = connection.prepareStatement("DELETE FROM leave_notifier WHERE guild_id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetLeaveNotifierChannel"));
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    // JoinMessage LeaveMessage
    private boolean joinLeaveMessageHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM join_leave_messages WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#joinLeaveMessageHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void checkAndPurgeJoinLeaveMessage() {
        if (joinLeaveMessageHasEntry()) {
            var joinMsg = getJoinMessage();
            var leaveMsg = getLeaveMessage();

            if (joinMsg == null && leaveMsg == null) {
                Connection connection = null;
                try {
                    connection = Servant.db.getHikari().getConnection();
                    var delete = connection.prepareStatement("DELETE FROM join_leave_messages WHERE guild_id=?");
                    delete.setLong(1, guildId);
                    delete.executeUpdate();
                } catch (SQLException e) {
                    Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#checkAndPurgeJoinLeaveMessage"));
                } finally {
                    closeQuietly(connection);
                }
            }
        }
    }

    public String getJoinMessage() {
        Connection connection = null;
        String msg = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT join_message FROM join_leave_messages WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                msg = resultSet.getString("join_message");
                if (msg.equalsIgnoreCase("empty")) msg = null;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getJoinMessage"));
        } finally {
            closeQuietly(connection);
        }

        return msg;
    }

    public void setJoinMessage(String msg) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (joinLeaveMessageHasEntry()) {
                var update = connection.prepareStatement("UPDATE join_leave_messages SET join_message=? WHERE guild_id=?");
                update.setString(1, msg);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO join_leave_messages (guild_id,join_message,leave_message) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, msg);
                insert.setString(3, "empty");
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setJoinMessage"));
        } finally {
            closeQuietly(connection);
        }

        checkAndPurgeJoinLeaveMessage();
    }

    public String getLeaveMessage() {
        Connection connection = null;
        String msg = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT leave_message FROM join_leave_messages WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                msg = resultSet.getString("leave_message");
                if (msg.equalsIgnoreCase("empty")) msg = null;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getLeaveMessage"));
        } finally {
            closeQuietly(connection);
        }

        return msg;
    }

    public void setLeaveMessage(String msg) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (joinLeaveMessageHasEntry()) {
                var update = connection.prepareStatement("UPDATE join_leave_messages SET leave_message=? WHERE guild_id=?");
                update.setString(1, msg);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO join_leave_messages (guild_id,join_message,leave_message) VALUES (?,?,?)");
                insert.setLong(1, guildId);
                insert.setString(2, "empty");
                insert.setString(3, msg);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setLeaveMessage"));
        } finally {
            closeQuietly(connection);
        }

        checkAndPurgeJoinLeaveMessage();
    }

    // Exp
    public int getUserRank(long userId) {
        Connection connection = null;
        int rank = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM user_exp WHERE guild_id=? ORDER BY exp DESC");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                rank = 1;
                do {
                    if (resultSet.getLong("user_id") == userId) break;
                    else rank++;
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getUserRank"));
        } finally {
            closeQuietly(connection);
        }

        return rank;
    }

    // Livestream
    private boolean streamerModeHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM streamer_mode WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#streamerModeHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public boolean isStreamerMode() {
        Connection connection = null;
        var isStreamerMode = true;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT is_streamer_mode FROM streamer_mode WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isStreamerMode = resultSet.getBoolean("is_streamer_mode");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#isStreamerMode"));
        } finally {
            closeQuietly(connection);
        }

        return isStreamerMode;
    }

    public void toggleStreamerMode() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (streamerModeHasEntry()) {
                var update = connection.prepareStatement("UPDATE streamer_mode SET is_streamer_mode=? WHERE guild_id=?");
                update.setBoolean(1, !isStreamerMode());
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO streamer_mode (guild_id,is_streamer_mode) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setBoolean(2, !isStreamerMode());
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#toggleStreamerMode"));
        } finally {
            closeQuietly(connection);
        }
    }

    private boolean streamChannelHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM stream_channel WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#streamChannelHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public long getStreamChannelId() {
        Connection connection = null;
        var channelId = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT channel_id FROM stream_channel WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) channelId = resultSet.getLong("channel_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getStreamChannelId"));
        } finally {
            closeQuietly(connection);
        }

        return channelId;
    }

    public void setStreamChannel(MessageChannel channel) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (streamChannelHasEntry()) {
                var update = connection.prepareStatement("UPDATE stream_channel SET channel_id=? WHERE guild_id=?");
                update.setLong(1, channel.getIdLong());
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO stream_channel (guild_id,channel_id) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channel.getIdLong());
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setStreamChannel"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetStreamChannel() {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (streamChannelHasEntry()) {
                var delete = connection.prepareStatement("DELETE FROM stream_channel WHERE guild_id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetStreamChannel"));
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    private boolean streamingRoleHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM streaming_role WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#streamingRoleHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public long getStreamingRoleId() {
        Connection connection = null;
        var role = 0L;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT role_id FROM streaming_role WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) role = resultSet.getLong("role_id");
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getStreamingRoleId"));
        } finally {
            closeQuietly(connection);
        }

        return role;
    }

    public void setStreamingRole(long roleId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (streamingRoleHasEntry()) {
                var update = connection.prepareStatement("UPDATE streaming_role SET role_id=? WHERE guild_id=?");
                update.setLong(1, roleId);
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO streaming_role (guild_id,role_id) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, roleId);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setStreamingRole"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetStreamingRole() {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (streamingRoleHasEntry()) {
                var delete = connection.prepareStatement("DELETE FROM streaming_role WHERE guild_id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetStreamingRole"));
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    private boolean isStreamer(long userId) {
        Connection connection = null;
        var isStreamer = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM streamers WHERE guild_id=? AND user_id=?");
            select.setLong(1, guildId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isStreamer = true;
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#isStreamer"));
        } finally {
            closeQuietly(connection);
        }

        return isStreamer;
    }

    public List<Long> getStreamers() {
        Connection connection = null;
        var streamers = new ArrayList<Long>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT user_id FROM streamers WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) do streamers.add(resultSet.getLong("user_id")); while (resultSet.next());
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getStreamers"));
        } finally {
            closeQuietly(connection);
        }

        return streamers;
    }

    public void setStreamer(long userId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("INSERT INTO streamers (guild_id,user_id) VALUES (?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, userId);
            insert.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setStreamer"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean unsetStreamer(long userId) {
        Connection connection = null;
        var wasUnset = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (isStreamer(userId)) {
                var delete = connection.prepareStatement("DELETE FROM streamers WHERE guild_id=? AND user_id=?");
                delete.setLong(1, guildId);
                delete.setLong(2, userId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetStreamer"));
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    // Giveaway
    public boolean isGiveaway(long channelId, long messageId) {
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
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#isGiveaway"));
        } finally {
            closeQuietly(connection);
        }

        return isGiveaway;
    }

    public void insertGiveawayToDb(long channelId, long messageId, long hostId, String prize, Timestamp time, int amountWinners) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var preparedStatement = connection.prepareStatement("INSERT INTO giveawaylist(guild_id,channel_id,message_id,host_id,prize,time,amount_winners) VALUES(?,?,?,?,?,?,?)");
            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, channelId);
            preparedStatement.setLong(3, messageId);
            preparedStatement.setLong(4, hostId);
            preparedStatement.setString(5, prize);
            preparedStatement.setTimestamp(6, time);
            preparedStatement.setInt(7, amountWinners);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#insertGiveawayToDb"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void deleteGiveawayFromDb(long channelId, long messageId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM giveawaylist WHERE guild_id=? AND channel_id=? AND message_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, channelId);
            delete.setLong(3, messageId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#deleteGiveawayFromDb"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void purgeGiveawaysFromChannel(long channelId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM giveawaylist WHERE guild_id=? AND channel_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, channelId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#purgeGiveawaysFromChannel"));
        } finally {
            closeQuietly(connection);
        }
    }

    public void purgeGiveaways() {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM giveawaylist WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#purgeGiveaways"));
        } finally {
            closeQuietly(connection);
        }
    }

    public String getCurrentGiveaways(JDA jda, String lang) {
        Connection connection = null;
        var currentGiveaways = LanguageHandler.get(lang, "giveaway_nocurrent");

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM giveawaylist WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) currentGiveaways = getRunningGiveaways(jda, resultSet, lang);
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getCurrentGiveaways"));
        } finally {
            closeQuietly(connection);
        }

        return currentGiveaways;
    }

    private String getRunningGiveaways(JDA jda, ResultSet resultSet, String lang) throws SQLException {
        var giveawayList = new StringBuilder();
        var guild = jda.getGuildById(resultSet.getLong("guild_id"));
        if (guild == null) return giveawayList.toString();
        var tc = guild.getTextChannelById(resultSet.getLong("channel_id"));
        if (tc == null) return giveawayList.toString();
        do giveawayList.append("- ")
                .append(tc.getAsMention())
                .append(" ").append(LanguageHandler.get(lang, "giveaway_messageid")).append(" ").append(resultSet.getLong("message_id"))
                .append(" ").append(LanguageHandler.get(lang, "giveaway_prize")).append(" ").append(resultSet.getString("prize"))
                .append("\n");
        while (resultSet.next());

        return giveawayList.toString();
    }

    // RemindMe
    public RemindMe getRemindMe(int aiNumber) {
        Connection connection = null;
        RemindMe remindMe = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM remindme_new WHERE ai_number=?");
            select.setInt(1, aiNumber);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                remindMe = new RemindMe(
                        resultSet.getInt("ai_number"),
                        resultSet.getLong("guild_id"),
                        resultSet.getLong("channel_id"),
                        resultSet.getLong("message_id"),
                        resultSet.getLong("user_id"),
                        resultSet.getTimestamp("event_time"),
                        resultSet.getString("topic")
                );
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getRemindMe"));
        } finally {
            closeQuietly(connection);
        }

        return remindMe;
    }

    public int setRemindMe(long channelId, long messageId, long userId, Timestamp eventTime, String topic) {
        Connection connection = null;
        var aiNumber = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("INSERT INTO remindme_new (guild_id,channel_id,message_id,user_id,event_time,topic) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            insert.setLong(1, guildId);
            insert.setLong(2, channelId);
            insert.setLong(3, messageId);
            insert.setLong(4, userId);
            insert.setTimestamp(5, eventTime);
            insert.setString(6, topic);
            insert.executeUpdate();

            var resultSet = insert.getGeneratedKeys();
            if (resultSet.first()) aiNumber = resultSet.getInt(1);
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setRemindMe"));
        } finally {
            closeQuietly(connection);
        }

        return aiNumber;
    }

    public void unsetRemindMe(int aiNumber) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("DELETE FROM remindme_new WHERE ai_number=?");
            insert.setInt(1, aiNumber);
            insert.executeUpdate();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetRemindMe"));
        } finally {
            closeQuietly(connection);
        }
    }

    // Log
    private boolean logHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM log WHERE guild_id=?");
            select.setLong(1, guildId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#logHasEntry"));
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public HashMap<String, Boolean> getLogEvents() {
        Connection connection = null;
        var logSettings = new HashMap<String, Boolean>();

        if (logHasEntry()) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var select = connection.prepareStatement("SELECT * FROM log WHERE guild_id=?");
                select.setLong(1, guildId);
                var resultSet = select.executeQuery();
                if (resultSet.first()) {
                    logSettings.put("boost_count", resultSet.getBoolean("boost_count"));
                    logSettings.put("member_join", resultSet.getBoolean("member_join"));
                    logSettings.put("member_leave", resultSet.getBoolean("member_leave"));
                    logSettings.put("role_add", resultSet.getBoolean("role_add"));
                    logSettings.put("role_remove", resultSet.getBoolean("role_remove"));
                }
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getLogSettings"));
            } finally {
                closeQuietly(connection);
            }
        }

        return logSettings;
    }

    public boolean logIsEnabled(String eventName) {
        Connection connection = null;
        var isEnabled = true;

        if (logHasEntry()) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var select = connection.prepareStatement("SELECT * FROM log WHERE guild_id=?");
                select.setLong(1, guildId);
                var resultSet = select.executeQuery();
                if (resultSet.first()) isEnabled = resultSet.getBoolean(eventName);
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#logIsEnabled"));
            } finally {
                closeQuietly(connection);
            }
        }

        return isEnabled;
    }

    public long getLogChannelId() {
        Connection connection = null;
        var logChannelId = 0L;

        if (logHasEntry()) {
            try {
                connection = Servant.db.getHikari().getConnection();
                var select = connection.prepareStatement("SELECT * FROM log WHERE guild_id=?");
                select.setLong(1, guildId);
                var resultSet = select.executeQuery();
                if (resultSet.first()) logChannelId = resultSet.getLong("channel_id");
            } catch (SQLException e) {
                Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#getLogSettings"));
            } finally {
                closeQuietly(connection);
            }
        }

        return logChannelId;
    }

    public void setLogChannel(MessageChannel channel) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (logHasEntry()) {
                var update = connection.prepareStatement("UPDATE log SET channel_id=? WHERE guild_id=?");
                update.setLong(1, channel.getIdLong());
                update.setLong(2, guildId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO log " +
                        "(guild_id,channel_id,boost_count,member_join,member_leave,role_add,role_remove) " +
                        "VALUES (?,?,?,?,?,?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, channel.getIdLong());
                insert.setBoolean(3, true);
                insert.setBoolean(4, true);
                insert.setBoolean(5, true);
                insert.setBoolean(6, true);
                insert.setBoolean(7, true);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#setLogChannel"));
        } finally {
            closeQuietly(connection);
        }
    }

    public boolean logToggle(String eventName) {
        Connection connection = null;
        var wasToggled = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (logHasEntry()) {
                var status = logIsEnabled(eventName);
                var update = connection.prepareStatement("UPDATE log SET " + eventName + "=? WHERE guild_id=?");
                update.setBoolean(1, !status);
                update.setLong(2, guildId);
                update.executeUpdate();
                wasToggled = true;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#logToggle"));
        } finally {
            closeQuietly(connection);
        }

        return wasToggled;
    }

    public boolean unsetLog() {
        Connection connection = null;
        var wasUnset = false;

        try {
            if (logHasEntry()) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM log WHERE guild_id=?");
                delete.setLong(1, guildId);
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "Server#unsetLog"));
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }
}
