package moderation.guild;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import servant.Database;
import servant.Servant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Guild {
    private long guildId;
    private String offset;
    private String prefix;

    public Guild(long guildId) throws SQLException {
        this.guildId = guildId;
        thisOffset();
        thisPrefix();
    }

    // Lobby.
    public boolean toggleVoiceText() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM guild_settings WHERE guild_id=? AND setting=?");
        select.setLong(1, guildId);
        select.setString(2, "voicetext");
        var resultSet = select.executeQuery();
        boolean wasEnabled;
        if (resultSet.first()) {
            if (resultSet.getString("value").equals("on")) wasEnabled = setVoiceText(false);
            else wasEnabled = setVoiceText(true);
        } else wasEnabled = setVoiceText(true);
        return wasEnabled;
    }

    private boolean setVoiceText(boolean enable) throws SQLException {
        var connection = Database.getConnection();
        if (voiceTextHasEntry()) {
            //  Update.
            var update = connection.prepareStatement("UPDATE guild_settings SET value=? WHERE guild_id=? AND setting=?");
            update.setString(1, enable ? "on" : "off");
            update.setLong(2, guildId);
            update.setString(3, "voicetext");
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO guild_settings (guild_id,setting,value) VALUES (?,?,?)");
            insert.setLong(1, guildId);
            insert.setString(2, "voicetext");
            insert.setString(3, enable ? "on" : "off");
            insert.executeUpdate();
        }
        connection.close();
        return enable;
    }

    private boolean voiceTextHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM guild_settings WHERE guild_id=? AND setting=?");
        select.setLong(1, guildId);
        select.setString(2, "voicetext");
        var resultSet = select.executeQuery();
        var voiceTextHasEntry = false;
        if (resultSet.first()) voiceTextHasEntry = true;
        connection.close();
        return voiceTextHasEntry;
    }

    public boolean isVoiceText() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM guild_settings WHERE guild_id=? AND setting=?");
        select.setLong(1, guildId);
        select.setString(2, "voicetext");
        var resultSet = select.executeQuery();
        var isVoiceText = false;
        if (resultSet.first()) isVoiceText = resultSet.getString("value").equals("on");
        connection.close();
        return isVoiceText;
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
        if (prefixHasEntry()) {
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
    public String getPrefix() { return prefix; }

    private boolean prefixHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM guild_settings WHERE guild_id=? AND setting=?");
        select.setLong(1, guildId);
        select.setString(2, "prefix");
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    private void thisPrefix() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT value FROM guild_settings WHERE guild_id=? AND setting=?");
        select.setLong(1, guildId);
        select.setString(2, "prefix");
        var resultSet = select.executeQuery();
        String prefix = null;
        if (resultSet.first()) prefix = resultSet.getString("value");
        if (prefix == null) this.prefix = Servant.config.getDefaultPrefix();
        else this.prefix = prefix;
        connection.close();
    }

    public void setPrefix(String prefix) throws SQLException {
        this.prefix = prefix;

        var connection = Database.getConnection();
        if (prefixHasEntry()) {
            //  Update.
            var update = connection.prepareStatement("UPDATE guild_settings SET value=? WHERE guild_id=? AND setting=?");
            update.setString(1, prefix);
            update.setLong(2, guildId);
            update.setString(3, "prefix");
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO guild_settings (guild_id,setting,value) VALUES (?,?,?)");
            insert.setLong(1, guildId);
            insert.setString(2, "prefix");
            insert.setString(3, prefix);
            insert.executeUpdate();
        }
        connection.close();
    }

    boolean unsetPrefix() throws SQLException {
        this.prefix = Servant.config.getDefaultPrefix();

        var connection = Database.getConnection();
        if (prefixHasEntry()) {
            //  Delete.
            var delete = connection.prepareStatement("DELETE FROM guild_settings WHERE guild_id=? AND setting=?");
            delete.setLong(1, guildId);
            delete.setString(2, "prefix");
            delete.executeUpdate();
            connection.close();
            return true;
        } else {
            // Nothing to delete.
            connection.close();
            return false;
        }
    }

    // Color.
    public String getOffset() { return offset; }

    private boolean offsetHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM guild_settings WHERE guild_id=? AND setting=?");
        select.setLong(1, guildId);
        select.setString(2, "offset");
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    private void thisOffset() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT value FROM guild_settings WHERE guild_id=? AND setting=?");
        select.setLong(1, guildId);
        select.setString(2, "offset");
        var resultSet = select.executeQuery();
        String offset = null;
        if (resultSet.first()) offset = resultSet.getString("value");
        if (offset == null) this.offset = Servant.config.getDefaultOffset();
        else this.offset = offset;
        connection.close();
    }

    void setOffset(String offset) throws SQLException {
        this.offset = offset;

        var connection = Database.getConnection();
        if (offsetHasEntry()) {
            //  Update.
            var update = connection.prepareStatement("UPDATE guild_settings SET value=? WHERE guild_id=? AND setting=?");
            update.setString(1, offset);
            update.setLong(2, guildId);
            update.setString(3, "offset");
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO guild_settings (guild_id,setting,value) VALUES (?,?,?)");
            insert.setLong(1, guildId);
            insert.setString(2, "offset");
            insert.setString(3, offset);
            insert.executeUpdate();
        }
        connection.close();
    }

    boolean unsetOffset() throws SQLException {
        this.offset = Servant.config.getDefaultOffset();

        var connection = Database.getConnection();
        if (offsetHasEntry()) {
            //  Delete.
            var delete = connection.prepareStatement("DELETE FROM guild_settings WHERE guild_id=? AND setting=?");
            delete.setLong(1, guildId);
            delete.setString(2, "offset");
            delete.executeUpdate();
            connection.close();
            return true;
        } else {
            // Nothing to delete.
            connection.close();
            return false;
        }
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

    public Role getAutorole() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT role_id FROM autorole WHERE guild_id=?");
        select.setLong(1, guildId);
        var resultSet = select.executeQuery();

        Role role = null;
        if (resultSet.first()) {
            var roleId = resultSet.getLong("role_id");
            role = Servant.jda.getGuildById(guildId).getRoleById(roleId);
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

    public void setAutorole(Role role) throws SQLException {
        var connection = Database.getConnection();
        if (autoroleHasEntry()) {
            // Update.
            var update = connection.prepareStatement("UPDATE autorole SET role_id=? WHERE guild_id=?");
            update.setLong(1, role.getIdLong());
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO autorole (guild_id,role_id) VALUES (?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, role.getIdLong());
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

    // JoinNotifier
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

    public long getStreamChannel() throws SQLException {
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
}
