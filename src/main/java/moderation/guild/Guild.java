// Author: Tancred423 (https://github.com/Tancred423)
package moderation.guild;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import servant.Database;
import servant.Servant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Guild {
    private long guildId;

    public Guild(long guildId) {
        this.guildId = guildId;
    }

    // BestOfQuote
    private boolean bestOfQuoteHasEntry() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM best_of_quote WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public Emote getBestOfQuoteEmote() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM best_of_quote WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
        Emote emote = null;
        if (resultSet.first()) {
            var guildId = resultSet.getLong("emote_guild_id");
            var emoteId = resultSet.getLong("emote_id");
            if (guildId != 0 && emoteId != 0) emote = Servant.jda.getGuildById(guildId).getEmoteById(emoteId);
        }
        connection.close();
        return emote;
    }

    public String getBestOfQuoteEmoji() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT emoji FROM best_of_quote WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
        String emoji = "";
        if (resultSet.first()) emoji = resultSet.getString("emoji");
        if (emoji.isEmpty()) emoji = null;
        connection.close();
        return emoji;
    }

    public TextChannel getBestOfQuoteChannel() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT channel_id FROM best_of_quote WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
        TextChannel channel = null;
        if (resultSet.first())
            channel = Servant.jda.getGuildById(guildId).getTextChannelById(resultSet.getLong("channel_id"));
        connection.close();
        return channel;
    }

    public int getBestOfQuoteNumber() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT number FROM best_of_quote WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
        int number = 0;
        if (resultSet.first())
            number = resultSet.getInt("number");
        connection.close();
        return number;
    }

    public int getBestOfQuotePercentage() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT percentage FROM best_of_quote WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
        int percentage = 0;
        if (resultSet.first())
            percentage = resultSet.getInt("percentage");
        connection.close();
        return percentage;
    }

    public void setBestOfQuoteChannel(long channelId) throws SQLException {
        Connection connection = Database.getConnection();
        if (bestOfQuoteHasEntry()) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE best_of_quote SET channel_id=? WHERE guild_id=?");
            update.setLong(1, channelId);
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO best_of_quote (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, channelId);
            insert.setInt(3, 0);
            insert.setInt(4, 0);
            insert.setString(5, "");
            insert.setLong(6, 0);
            insert.setLong(7, 0);
            insert.executeUpdate();
        }
        connection.close();
    }

    public void setBestOfQuoteEmote(long emoteGuildId, long emoteId) throws SQLException {
        Connection connection = Database.getConnection();
        if (bestOfQuoteHasEntry()) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE best_of_quote SET emoji=?, emote_guild_id=?, emote_id=? WHERE guild_id=?");
            update.setString(1, "");
            update.setLong(2, emoteGuildId);
            update.setLong(3, emoteId);
            update.setLong(4, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO best_of_quote (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, 0);
            insert.setInt(3, 0);
            insert.setInt(4, 0);
            insert.setString(5, "");
            insert.setLong(6, emoteGuildId);
            insert.setLong(7, emoteId);
            insert.executeUpdate();
        }
        connection.close();
    }

    public void setBestOfQuoteEmoji(String emoji) throws SQLException {
        Connection connection = Database.getConnection();
        if (bestOfQuoteHasEntry()) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE best_of_quote SET emoji=?, emote_guild_id=?, emote_id=? WHERE guild_id=?");
            update.setString(1, emoji);
            update.setLong(2, 0);
            update.setLong(3, 0);
            update.setLong(4, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO best_of_quote (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, 0);
            insert.setInt(3, 0);
            insert.setInt(4, 0);
            insert.setString(5, emoji);
            insert.setLong(6, 0);
            insert.setLong(7, 0);
            insert.executeUpdate();
        }
        connection.close();
    }

    public void setBestOfQuoteNumber(int number) throws SQLException {
        Connection connection = Database.getConnection();
        if (bestOfQuoteHasEntry()) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE best_of_quote SET number=? WHERE guild_id=?");
            update.setInt(1, number);
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO best_of_quote (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, 0);
            insert.setInt(3, number);
            insert.setInt(4, 0);
            insert.setString(5, "");
            insert.setLong(6, 0);
            insert.setLong(7, 0);
            insert.executeUpdate();
        }
        connection.close();
    }

    public void setBestOfQuotePercentage(int percentage) throws SQLException {
        Connection connection = Database.getConnection();
        if (bestOfQuoteHasEntry()) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE best_of_quote SET percentage=? WHERE guild_id=?");
            update.setInt(1, percentage);
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO best_of_quote (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, 0);
            insert.setInt(3, 0);
            insert.setInt(4, percentage);
            insert.setString(5, "");
            insert.setLong(6, 0);
            insert.setLong(7, 0);
            insert.executeUpdate();
        }
        connection.close();
    }

    public void addBestOfQuoteBlacklist(long messageId) throws SQLException {
        if (!bestOfQuoteIsBlacklisted(messageId)) {
            Connection connection = Database.getConnection();
            PreparedStatement insert = connection.prepareStatement("INSERT INTO best_of_quote_bl (message_id) VALUES (?)");
            insert.setLong(1, messageId);
            insert.executeUpdate();
            connection.close();
        }
    }

    public boolean bestOfQuoteIsBlacklisted(long messageId) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM best_of_quote_bl WHERE message_id=?");
        select.setLong(1, messageId);
        ResultSet resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    // BestOfImage
    private boolean bestOfImageHasEntry() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM best_of_image WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public Emote getBestOfImageEmote() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM best_of_image WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
        Emote emote = null;
        if (resultSet.first()) {
            var guildId = resultSet.getLong("emote_guild_id");
            var emoteId = resultSet.getLong("emote_id");
            if (guildId != 0 && emoteId != 0) emote = Servant.jda.getGuildById(guildId).getEmoteById(emoteId);
        }
        connection.close();
        return emote;
    }

    public String getBestOfImageEmoji() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT emoji FROM best_of_image WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
        String emoji = "";
        if (resultSet.first()) emoji = resultSet.getString("emoji");
        if (emoji.isEmpty()) emoji = null;
        connection.close();
        return emoji;
    }

    public TextChannel getBestOfImageChannel() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT channel_id FROM best_of_image WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
        TextChannel channel = null;
        if (resultSet.first())
            channel = Servant.jda.getGuildById(guildId).getTextChannelById(resultSet.getLong("channel_id"));
        connection.close();
        return channel;
    }

    public int getBestOfImageNumber() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT number FROM best_of_image WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
        int number = 0;
        if (resultSet.first())
            number = resultSet.getInt("number");
        connection.close();
        return number;
    }

    public int getBestOfImagePercentage() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT percentage FROM best_of_image WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
        int percentage = 0;
        if (resultSet.first())
            percentage = resultSet.getInt("percentage");
        connection.close();
        return percentage;
    }

    public void setBestOfImageChannel(long channelId) throws SQLException {
        Connection connection = Database.getConnection();
        if (bestOfImageHasEntry()) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE best_of_image SET channel_id=? WHERE guild_id=?");
            update.setLong(1, channelId);
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, channelId);
            insert.setInt(3, 0);
            insert.setInt(4, 0);
            insert.setString(5, "");
            insert.setLong(6, 0);
            insert.setLong(7, 0);
            insert.executeUpdate();
        }
        connection.close();
    }

    public void setBestOfImageEmote(long emoteGuildId, long emoteId) throws SQLException {
        Connection connection = Database.getConnection();
        if (bestOfImageHasEntry()) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE best_of_image SET emoji=?, emote_guild_id=?, emote_id=? WHERE guild_id=?");
            update.setString(1, "");
            update.setLong(2, emoteGuildId);
            update.setLong(3, emoteId);
            update.setLong(4, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, 0);
            insert.setInt(3, 0);
            insert.setInt(4, 0);
            insert.setString(5, "");
            insert.setLong(6, emoteGuildId);
            insert.setLong(7, emoteId);
            insert.executeUpdate();
        }
        connection.close();
    }

    public void setBestOfImageEmoji(String emoji) throws SQLException {
        Connection connection = Database.getConnection();
        if (bestOfImageHasEntry()) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE best_of_image SET emoji=?, emote_guild_id=?, emote_id=? WHERE guild_id=?");
            update.setString(1, emoji);
            update.setLong(2, 0);
            update.setLong(3, 0);
            update.setLong(4, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, 0);
            insert.setInt(3, 0);
            insert.setInt(4, 0);
            insert.setString(5, emoji);
            insert.setLong(6, 0);
            insert.setLong(7, 0);
            insert.executeUpdate();
        }
        connection.close();
    }

    public void setBestOfImageNumber(int number) throws SQLException {
        Connection connection = Database.getConnection();
        if (bestOfImageHasEntry()) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE best_of_image SET number=? WHERE guild_id=?");
            update.setInt(1, number);
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, 0);
            insert.setInt(3, number);
            insert.setInt(4, 0);
            insert.setString(5, "");
            insert.setLong(6, 0);
            insert.setLong(7, 0);
            insert.executeUpdate();
        }
        connection.close();
    }

    public void setBestOfImagePercentage(int percentage) throws SQLException {
        Connection connection = Database.getConnection();
        if (bestOfImageHasEntry()) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE best_of_image SET percentage=? WHERE guild_id=?");
            update.setInt(1, percentage);
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO best_of_image (guild_id,channel_id,number,percentage,emoji,emote_guild_id,emote_id) VALUES (?,?,?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, 0);
            insert.setInt(3, 0);
            insert.setInt(4, percentage);
            insert.setString(5, "");
            insert.setLong(6, 0);
            insert.setLong(7, 0);
            insert.executeUpdate();
        }
        connection.close();
    }

    public void addBestOfImageBlacklist(long messageId) throws SQLException {
        if (!bestOfImageIsBlacklisted(messageId)) {
            Connection connection = Database.getConnection();
            PreparedStatement insert = connection.prepareStatement("INSERT INTO best_of_image_bl (message_id) VALUES (?)");
            insert.setLong(1, messageId);
            insert.executeUpdate();
            connection.close();
        }
    }

    public boolean bestOfImageIsBlacklisted(long messageId) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM best_of_image_bl WHERE message_id=?");
        select.setLong(1, messageId);
        ResultSet resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    // Birthday Gratulation
    public boolean wasGratulated(long userId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT was_gratulated FROM birthday_gratulation WHERE guild_id=? and user_id=?");
        select.setLong(1, guildId);
        select.setLong(2, userId);
        var resultSet = select.executeQuery();
        boolean wasGratulated = false;
        if (resultSet.first()) wasGratulated = resultSet.getBoolean("was_gratulated");
        connection.close();
        return wasGratulated;
    }

    private boolean birthdayGratulationHasEntry(long userId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM birthday_gratulation WHERE guild_id=? and user_id=?");
        select.setLong(1, guildId);
        select.setLong(2, userId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public void setGratulated(long userId) throws SQLException {
        if (!birthdayGratulationHasEntry(userId)) {
            var connection = Database.getConnection();
            var insert = connection.prepareStatement("INSERT INTO birthday_gratulation (guild_id,user_id,was_gratulated) VALUES (?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, userId);
            insert.setBoolean(3, true);
            insert.executeUpdate();
            connection.close();
        }
    }

    public void unsetGratulated(long userId) throws SQLException {
        if (birthdayGratulationHasEntry(userId)) {
            var connection = Database.getConnection();
            var delete = connection.prepareStatement("DELETE FROM birthday_gratulation WHERE guild_id=? AND user_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, userId);
            delete.executeUpdate();
            connection.close();
        }
    }

    private void unsetGratulateds() throws SQLException {
        var connection = Database.getConnection();
        var delete = connection.prepareStatement("DELETE FROM birthday_gratulation WHERE guild_id=?");
        delete.setLong(1, guildId);
        delete.executeUpdate();
        connection.close();
    }

    // Birthday Message
    public long getBirthdayMessageChannelId() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT channel_id FROM birthday_messages WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        long channelId = 0;
        if (resultSet.first()) channelId = resultSet.getLong("channel_id");
        connection.close();
        return channelId;
    }

    public long getBirthdayMessageMessageId() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT message_id FROM birthday_messages WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        long messageId = 0;
        if (resultSet.first()) messageId = resultSet.getLong("message_id");
        connection.close();
        return messageId;
    }

    public long getBirthdayMessageAuthorId() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT user_id FROM birthday_messages WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        long messageId = 0;
        if (resultSet.first()) messageId = resultSet.getLong("user_id");
        connection.close();
        return messageId;
    }

    public boolean birthdayMessagesHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM birthday_messages WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public void setBirthdayMessage(long channelId, long messageId, long userId) throws SQLException {
        var connection = Database.getConnection();
        if (birthdayMessagesHasEntry()) {
            //  Update.
            var update = connection.prepareStatement("UPDATE birthday_messages SET channel_id=?, message_id=?, user_id=? WHERE guild_id=?");
            update.setLong(1, channelId);
            update.setLong(2, messageId);
            update.setLong(3, userId);
            update.setLong(4, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO birthday_messages (guild_id,channel_id,message_id,user_id) VALUES (?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, channelId);
            insert.setLong(3, messageId);
            insert.setLong(4, userId);
            insert.executeUpdate();
        }
        connection.close();
    }

    public void unsetBirthdayMessage() throws SQLException {
        if (birthdayMessagesHasEntry()) {
            var connection = Database.getConnection();
            var delete = connection.prepareStatement("DELETE FROM birthday_messages WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
            connection.close();
        }
    }

    // Guild
    private boolean guildHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM guild WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    // Birthday
    public void purgeBirthday() throws SQLException {
        unsetBirthdayMessage();
        unsetBirthdayChannelId();
        unsetBirthdays();
        unsetGratulateds();
    }

    public Map<Long, String> getBirthdays() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM birthdays WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        Map<Long, String> birthdays = new HashMap<>();
        if (resultSet.first()) {
            do birthdays.put(resultSet.getLong("user_id"), resultSet.getString("birthday"));
            while (resultSet.next());
        }
        connection.close();
        return birthdays;
    }

    private boolean birthdaysHasEntry(long userId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT birthday FROM birthdays WHERE guild_id=? AND user_id=?");
        select.setLong(1, guildId);
        select.setLong(2, userId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public void setBirthday(long userId, String birthday) throws SQLException {
        var connection = Database.getConnection();
        if (birthdaysHasEntry(userId)) {
            //  Update.
            var update = connection.prepareStatement("UPDATE birthdays SET birthday=? WHERE guild_id=? AND user_id=?");
            update.setString(1, birthday);
            update.setLong(2, guildId);
            update.setLong(3, userId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO birthdays (guild_id,user_id,birthday) VALUES (?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, userId);
            insert.setString(3, birthday);
            insert.executeUpdate();
        }
        connection.close();
    }

    public boolean unsetBirthday(long userId) throws SQLException {
        if (birthdaysHasEntry(userId)) {
            var connection = Database.getConnection();
            var delete = connection.prepareStatement("DELETE FROM birthdays WHERE guild_id=? AND user_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, userId);
            delete.executeUpdate();
            connection.close();
            return true;
        } else return false;
    }

    private void unsetBirthdays() throws SQLException {
        var connection = Database.getConnection();
        var delete = connection.prepareStatement("DELETE FROM birthdays WHERE guild_id=?");
        delete.setLong(1, guildId);
        delete.executeUpdate();
        connection.close();
    }

    // Birthday Channel
    public long getBirthdayChannelId() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT birthday_channel_id FROM guild WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        long birthdayChannelId = 0;
        if (resultSet.first()) birthdayChannelId = resultSet.getLong("birthday_channel_id");
        connection.close();
        return birthdayChannelId;
    }

    public void setBirthdayChannelId(long birthdayChannelId) throws SQLException {
        var connection = Database.getConnection();
        if (guildHasEntry()) {
            //  Update.
            var update = connection.prepareStatement("UPDATE guild SET birthday_channel_id=? WHERE guild_id=?");
            update.setLong(1, birthdayChannelId);
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO guild (guild_id,prefix,offset,language,birthday_channel_id) VALUES (?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setString(2, Servant.config.getDefaultPrefix());
            insert.setString(3, "Z");
            insert.setString(4, Servant.config.getDefaultLanguage());
            insert.setLong(5, birthdayChannelId);
            insert.executeUpdate();
        }
        connection.close();
    }

    public void unsetBirthdayChannelId() throws SQLException {
        setBirthdayChannelId(0);
    }

    // Level Role
    public List<Long> getLevelRolesForLevel(int level) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM level_role WHERE guild_id=? ORDER BY level DESC");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        List<Long> roleId = new ArrayList<>();
        if (resultSet.first())
            do if (level >= resultSet.getInt("level")) roleId.add(resultSet.getLong("role_id"));
            while (resultSet.next());
        connection.close();
        return roleId;
    }

    public List<Long> getLevelRole(int level) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT role_id FROM level_role WHERE guild_id=? AND level=?");
        select.setLong(1, guildId);
        select.setInt(2, level);
        var resultSet = select.executeQuery();
        List<Long> roleId = new ArrayList<>();
        if (resultSet.first()) do roleId.add(resultSet.getLong("role_id")); while (resultSet.next());
        connection.close();
        return roleId;
    }

    public Map<Integer, Long> getLevelRoles() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM level_role WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        Map<Integer, Long> levelRoles = new HashMap<>();
        if (resultSet.first())
            do levelRoles.put(resultSet.getInt("level"), resultSet.getLong("role_id"));
            while(resultSet.next());
        connection.close();
        return levelRoles;
    }

    public boolean setLevelRole(int level, long roleId) throws SQLException {
        if (getLevelRole(level).contains(roleId)) return false;
        var connection = Database.getConnection();
        var insert = connection.prepareStatement("INSERT INTO level_role (guild_id,level,role_id) VALUES (?,?,?)");
        insert.setLong(1, guildId);
        insert.setInt(2, level);
        insert.setLong(3, roleId);
        insert.executeUpdate();
        connection.close();
        return true;
    }

    public void unsetLevelRole(int level, long roleId) throws SQLException {
        var connection = Database.getConnection();
        var delete = connection.prepareStatement("DELETE FROM level_role WHERE guild_id=? AND level=? AND role_id=?");
        delete.setLong(1, guildId);
        delete.setInt(2, level);
        delete.setLong(3, roleId);
        delete.executeUpdate();
        connection.close();
    }

    // Language
    public String getLanguage() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT language FROM guild WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        String language = null;
        if (resultSet.first()) language = resultSet.getString("language");
        if (language == null) language = Servant.config.getDefaultLanguage();
        else if (language.isEmpty()) language = Servant.config.getDefaultLanguage();
        connection.close();
        return language;
    }

    public void setLanguage(String language) throws SQLException {
        var connection = Database.getConnection();
        if (guildHasEntry()) {
            //  Update.
            var update = connection.prepareStatement("UPDATE guild SET language=? WHERE guild_id=?");
            update.setString(1, language);
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO guild (guild_id,prefix,offset,language,birthday_channel_id) VALUES (?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setString(2, Servant.config.getDefaultPrefix());
            insert.setString(3, Servant.config.getDefaultOffset());
            insert.setString(4, language);
            insert.setLong(5, 0);
            insert.executeUpdate();
        }
        connection.close();
    }

    void unsetLanguage() throws SQLException {
        setLanguage(Servant.config.getDefaultLanguage());
    }

    // Lobby.
    private boolean lobbyHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM lobby WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public List<Long> getLobbies() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM lobby WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        List<Long> lobbies = new ArrayList<>();
        if (resultSet.first()) do lobbies.add(resultSet.getLong("channel_id")); while (resultSet.next());
        connection.close();
        return lobbies;
    }

    public void setLobby(long channelId) throws SQLException {
        var connection = Database.getConnection();
        var insert = connection.prepareStatement("INSERT INTO lobby (guild_id,channel_id) VALUES (?,?)");
        insert.setLong(1, guildId);
        insert.setLong(2, channelId);
        insert.executeUpdate();
        connection.close();
    }

    public boolean unsetLobby(long channelId) throws SQLException {
        var connection = Database.getConnection();
        if (lobbyHasEntry()) {
            //  Delete.
            var delete = connection.prepareStatement("DELETE FROM lobby WHERE guild_id=? and channel_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, channelId);
            delete.executeUpdate();
            connection.close();
            return true;
        } else {
            // Nothing to delete.
            connection.close();
            return false;
        }
    }

    // Prefix.
    public String getPrefix() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT prefix FROM guild WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        var prefix = Servant.config.getDefaultPrefix();
        if (resultSet.first()) prefix = resultSet.getString("prefix");
        connection.close();
        return prefix;
    }


    public void setPrefix(String prefix) throws SQLException {
        var connection = Database.getConnection();
        if (guildHasEntry()) {
            //  Update.
            var update = connection.prepareStatement("UPDATE guild SET prefix=? WHERE guild_id=?");
            update.setString(1, prefix);
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO guild (guild_id,prefix,offset,language,birthday_channel_id) VALUES (?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setString(2, prefix);
            insert.setString(3, Servant.config.getDefaultOffset());
            insert.setString(4, Servant.config.getDefaultLanguage());
            insert.setLong(5, 0L);
            insert.executeUpdate();
        }
        connection.close();
    }

    void unsetPrefix() throws SQLException {
        setPrefix(Servant.config.getDefaultPrefix());
    }

    // Color.
    public String getOffset() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT offset FROM guild WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        var offset = Servant.config.getDefaultOffset();
        if (resultSet.first()) offset = resultSet.getString("offset");
        offset = offset.equals("00:00") ? "Z" : offset;
        connection.close();
        return offset;
    }

    public void setOffset(String offset) throws SQLException {
        var connection = Database.getConnection();
        if (guildHasEntry()) {
            //  Update.
            var update = connection.prepareStatement("UPDATE guild SET offset=? WHERE guild_id=?");
            update.setString(1, offset);
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO guild (guild_id,prefix,offset,language,birthday_channel_id) VALUES (?,?,?,?,?)");
            insert.setLong(1, guildId);
            insert.setString(2, Servant.config.getDefaultPrefix());
            insert.setString(3, offset);
            insert.setString(4, Servant.config.getDefaultLanguage());
            insert.setLong(5, 0L);
            insert.executeUpdate();
        }
        connection.close();
    }

    void unsetOffset() throws SQLException {
        setOffset(Servant.config.getDefaultOffset());
    }

    // Feature counter.
    private boolean featureCountHasEntry(String key) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM feature_count WHERE id=? AND feature=?");
        select.setLong(1, guildId);
        select.setString(2, key);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    private int getFeatureCount(String feature) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT count FROM feature_count WHERE id=? AND feature=?");
        select.setLong(1, guildId);
        select.setString(2, feature.toLowerCase());
        var resultSet = select.executeQuery();
        int featureCount = 0;
        if (resultSet.first()) featureCount = resultSet.getInt("count");
        connection.close();
        return featureCount;
    }

    public void incrementFeatureCount(String feature) throws SQLException {
        var count = getFeatureCount(feature);
        var connection = Database.getConnection();
        if (featureCountHasEntry(feature)) {
            // Update.
            var update = connection.prepareStatement("UPDATE feature_count SET count=? WHERE id=? AND feature=?");
            update.setInt(1, count + 1);
            update.setLong(2, guildId);
            update.setString(3, feature.toLowerCase());
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO feature_count (id,feature,count) VALUES (?,?,?)");
            insert.setLong(1, guildId);
            insert.setString(2, feature.toLowerCase());
            insert.setInt(3, 1);
            insert.executeUpdate();
        }
        connection.close();
    }

    // Level
    public Map<Long, Integer> getLeaderboard() throws SQLException  {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM user_exp WHERE guild_id=? ORDER BY exp DESC");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();

        Map<Long, Integer> userExp = new LinkedHashMap<>();
        if (resultSet.first()) {
            var counter = 0;
            do {
                if (counter >= 10) break;
                userExp.put(resultSet.getLong("user_id"), resultSet.getInt("exp"));
                counter++;
            } while (resultSet.next());
        }
        if (userExp.isEmpty()) userExp = null;
        connection.close();
        return userExp;
    }

    // Autorole
    public boolean hasAutorole() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM autorole WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();

        var hasAutorole = false;
        if (resultSet.first()) hasAutorole = true;

        connection.close();
        return hasAutorole;
    }

    public Map<Role, Integer> getAutorole() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM autorole WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();

        Map<Role, Integer> role = new HashMap<>();
        if (resultSet.first()) {
            var roleId = resultSet.getLong("role_id");
            var delay = resultSet.getInt("delay");
            role.put(Servant.jda.getGuildById(guildId).getRoleById(roleId), delay);
        }

        connection.close();
        return role;
    }

    private boolean autoroleHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM autorole WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public void setAutorole(long roleId, int delay) throws SQLException {
        var connection = Database.getConnection();
        if (autoroleHasEntry()) {
            // Update.
            var update = connection.prepareStatement("UPDATE autorole SET role_id=?, delay=? WHERE guild_id=?");
            update.setLong(1, roleId);
            update.setInt(2, delay);
            update.setLong(3, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO autorole (guild_id,role_id,delay) VALUES (?,?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, roleId);
            insert.setInt(3, delay);
            insert.executeUpdate();
        }
        connection.close();
    }

    public boolean unsetAutorole() throws SQLException {
        if (autoroleHasEntry()) {
            // Delete.
            var connection = Database.getConnection();
            var delete = connection.prepareStatement("DELETE FROM autorole WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
            connection.close();
            return true;
        } else return false;
    }

    // MeidaOnlyChannel
    public boolean mediaOnlyChannelHasEntry(MessageChannel channel) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM mediaonlychannel WHERE guild_id=? AND channel_id=?");
        select.setLong(1, guildId);
        select.setLong(2, channel.getIdLong());
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public void addMediaOnlyChannel(MessageChannel channel) throws SQLException {
        var connection = Database.getConnection();
        var insert = connection.prepareStatement("INSERT INTO mediaonlychannel (guild_id,channel_id) VALUES (?,?)");
        insert.setLong(1, guildId);
        insert.setLong(2, channel.getIdLong());
        insert.executeUpdate();
        connection.close();
    }

    public boolean unsetMediaOnlyChannel(MessageChannel channel) throws SQLException {
        if (mediaOnlyChannelHasEntry(channel)) {
            var connection = Database.getConnection();
            var delete = connection.prepareStatement("DELETE FROM mediaonlychannel WHERE guild_id=? AND channel_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, channel.getIdLong());
            delete.executeUpdate();
            connection.close();
            return true;
        } else {
            return false;
        }
    }

    public void unsetMediaOnlyChannels() throws SQLException {
        var connection = Database.getConnection();
        var delete = connection.prepareStatement("DELETE FROM mediaonlychannel WHERE guild_id=?");
        delete.setLong(1, guildId);
        delete.executeUpdate();
        connection.close();
    }

    public List<MessageChannel> getMediaOnlyChannels() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM mediaonlychannel WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        List<MessageChannel> channels = new ArrayList<>();

        if (resultSet.first()) {
            do {
                channels.add(Servant.jda.getGuildById(guildId).getTextChannelById(resultSet.getLong("channel_id")));
            } while (resultSet.next());
        }

        if (channels.isEmpty()) channels = null;
        connection.close();
        return channels;
    }

    // Toggle
    private boolean toggleHasEntry(String feature) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM toggle WHERE guild_id=? AND feature=?");
        select.setLong(1, guildId);
        select.setString(2, feature);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public boolean getToggleStatus(String feature) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT is_enabled FROM toggle WHERE guild_id=? AND feature=?");
        select.setLong(1, guildId);
        select.setString(2, feature);
        var resultSet = select.executeQuery();
        boolean isEnabled;
        if (resultSet.first()) isEnabled = resultSet.getBoolean("is_enabled");
        else isEnabled = Servant.toggle.get(feature);

        connection.close();
        return isEnabled;
    }

    public void setToggleStatus(String feature, boolean status) throws SQLException {
        var connection = Database.getConnection();
        if (toggleHasEntry(feature)) {
            // Update.
            var update = connection.prepareStatement("UPDATE toggle SET is_enabled=? WHERE guild_id=? AND feature=?");
            update.setBoolean(1, status);
            update.setLong(2, guildId);
            update.setString(3, feature);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO toggle (guild_id,feature,is_enabled) VALUES (?,?,?)");
            insert.setLong(1, guildId);
            insert.setString(2, feature);
            insert.setBoolean(3, status);
            insert.executeUpdate();
        }
        connection.close();
    }

    // Join
    private boolean joinNotifierHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM join_notifier WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public MessageChannel getJoinNotifierChannel() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT channel_id FROM join_notifier WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        MessageChannel channel = null;
        if (resultSet.first()) channel = Servant.jda.getGuildById(guildId).getTextChannelById(resultSet.getLong("channel_id"));

        connection.close();
        return channel;
    }

    public void setJoinNotifierChannel(MessageChannel channel) throws SQLException {
        var connection = Database.getConnection();
        if (joinNotifierHasEntry()) {
            // Update.
            var update = connection.prepareStatement("UPDATE join_notifier SET channel_id=? WHERE guild_id=?");
            update.setLong(1, channel.getIdLong());
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO join_notifier (guild_id,channel_id) VALUES (?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, channel.getIdLong());
            insert.executeUpdate();
        }
        connection.close();
    }

    public boolean unsetJoinNotifierChannel() throws SQLException {
        if (joinNotifierHasEntry()) {
            var connection = Database.getConnection();
            var delete = connection.prepareStatement("DELETE FROM join_notifier WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
            connection.close();
            return true;
        } else {
            return false;
        }
    }

    // Leave
    private boolean leaveNotifierHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM leave_notifier WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public MessageChannel getLeaveNotifierChannel() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT channel_id FROM leave_notifier WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        MessageChannel channel = null;
        if (resultSet.first()) channel = Servant.jda.getGuildById(guildId).getTextChannelById(resultSet.getLong("channel_id"));

        connection.close();
        return channel;
    }

    public void setLeaveNotifierChannel(MessageChannel channel) throws SQLException {
        var connection = Database.getConnection();
        if (joinNotifierHasEntry()) {
            // Update.
            var update = connection.prepareStatement("UPDATE leave_notifier SET channel_id=? WHERE guild_id=?");
            update.setLong(1, channel.getIdLong());
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO leave_notifier (guild_id,channel_id) VALUES (?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, channel.getIdLong());
            insert.executeUpdate();
        }
        connection.close();
    }

    public boolean unsetLeaveNotifierChannel() throws SQLException {
        if (leaveNotifierHasEntry()) {
            var connection = Database.getConnection();
            var delete = connection.prepareStatement("DELETE FROM leave_notifier WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
            connection.close();
            return true;
        } else {
            return false;
        }
    }

    // Exp
    public int getUserRank(long userId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM user_exp WHERE guild_id=? ORDER BY exp DESC");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        int rank = 0;
        if (resultSet.first()) {
            rank = 1;
            do {
                if (resultSet.getLong("user_id") == userId) {
                    connection.close();
                    return rank;
                } else rank++;
            } while (resultSet.next());
        }

        connection.close();
        return rank;
    }

    // Stream
    private boolean streamerModeHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM streamer_mode WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public boolean isStreamerMode() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT is_streamer_mode FROM streamer_mode WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        boolean channel = true;
        if (resultSet.first()) channel = resultSet.getBoolean("is_streamer_mode");

        connection.close();
        return channel;
    }

    public void toggleStreamerMode() throws SQLException {
        var connection = Database.getConnection();
        if (streamerModeHasEntry()) {
            // Update.
            var update = connection.prepareStatement("UPDATE streamer_mode SET is_streamer_mode=? WHERE guild_id=?");
            update.setBoolean(1, !isStreamerMode());
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO streamer_mode (guild_id,is_streamer_mode) VALUES (?,?)");
            insert.setLong(1, guildId);
            insert.setBoolean(2, !isStreamerMode());
            insert.executeUpdate();
        }
        connection.close();
    }

    private boolean streamChannelHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM stream_channel WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public long getStreamChannelId() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT channel_id FROM stream_channel WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        long channel = 0;
        if (resultSet.first()) channel = resultSet.getLong("channel_id");

        connection.close();
        return channel;
    }

    public void setStreamChannel(MessageChannel channel) throws SQLException {
        var connection = Database.getConnection();
        if (streamChannelHasEntry()) {
            // Update.
            var update = connection.prepareStatement("UPDATE stream_channel SET channel_id=? WHERE guild_id=?");
            update.setLong(1, channel.getIdLong());
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO stream_channel (guild_id,channel_id) VALUES (?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, channel.getIdLong());
            insert.executeUpdate();
        }
        connection.close();
    }

    public boolean unsetStreamChannel() throws SQLException {
        if (streamChannelHasEntry()) {
            var connection = Database.getConnection();
            var delete = connection.prepareStatement("DELETE FROM stream_channel WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
            connection.close();
            return true;
        } else {
            return false;
        }
    }

    private boolean streamingRoleHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM streaming_role WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public long getStreamingRoleId() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT role_id FROM streaming_role WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        long role = 0;
        if (resultSet.first()) role = resultSet.getLong("role_id");

        connection.close();
        return role;
    }

    public void setStreamingRole(long roleId) throws SQLException {
        var connection = Database.getConnection();
        if (streamingRoleHasEntry()) {
            // Update.
            var update = connection.prepareStatement("UPDATE streaming_role SET role_id=? WHERE guild_id=?");
            update.setLong(1, roleId);
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO streaming_role (guild_id,role_id) VALUES (?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, roleId);
            insert.executeUpdate();
        }
        connection.close();
    }

    public boolean unsetStreamingRole() throws SQLException {
        if (streamingRoleHasEntry()) {
            var connection = Database.getConnection();
            var delete = connection.prepareStatement("DELETE FROM streaming_role WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
            connection.close();
            return true;
        } else {
            return false;
        }
    }

    private boolean isStreamer(long userId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM streamers WHERE guild_id=? AND user_id=?");
        select.setLong(1, guildId);
        select.setLong(2, userId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public List<Long> getStreamers() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT user_id FROM streamers WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();
        List<Long> streamers = new ArrayList<>();
        if (resultSet.first()) do streamers.add(resultSet.getLong("user_id")); while (resultSet.next());

        connection.close();
        return streamers;
    }

    public void setStreamer(long userId) throws SQLException {
        var connection = Database.getConnection();
        var insert = connection.prepareStatement("INSERT INTO streamers (guild_id,user_id) VALUES (?,?)");
        insert.setLong(1, guildId);
        insert.setLong(2, userId);
        insert.executeUpdate();
        connection.close();
    }

    public boolean unsetStreamer(long userId) throws SQLException {
        if (isStreamer(userId)) {
            var connection = Database.getConnection();
            var delete = connection.prepareStatement("DELETE FROM streamers WHERE guild_id=? AND user_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, userId);
            delete.executeUpdate();
            connection.close();
            return true;
        } else {
            return false;
        }
    }

    // Reaction Role
    public int setReactionRole(long guildId, long channelId, long messageId, String emoji, long emoteGuildId, long emoteId, long roleId) throws SQLException {
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
    }

    public int unsetReactionRole(long guildId, long channelId, long messageId, String emoji, long emoteGuildId, long emoteId) throws SQLException {
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
    }

    public long getRoleId(long guildId, long channelId, long messageId, String emoji, long emoteGuildId, long emoteId) throws SQLException {
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

    public boolean hasEntry(long guildId, long channelId, long messageId, String emoji, long emoteGuildId, long emoteId) throws SQLException {
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
