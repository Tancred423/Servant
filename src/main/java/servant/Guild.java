package servant;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Guild {
    private long guildId;
    private String offset;

    public Guild(long guildId) throws SQLException {
        this.guildId = guildId;
        thisOffset();
    }

    // Color.
    public String getOffset() { return offset; }

    private boolean offsetHasEntry() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM guild_settings WHERE guild_id=? AND setting=?");
        select.setLong(1, guildId);
        select.setString(2, "offset");
        ResultSet resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    private void thisOffset() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT value FROM guild_settings WHERE guild_id=? AND setting=?");
        select.setLong(1, guildId);
        select.setString(2, "offset");
        ResultSet resultSet = select.executeQuery();
        String prefix = null;
        if (resultSet.first()) prefix = resultSet.getString("value");
        if (prefix == null) this.offset = Servant.config.getDefaultOffset();
        else this.offset = prefix;
        connection.close();
    }

    public void setOffset(String offset) throws SQLException {
        this.offset = offset;

        Connection connection = Database.getConnection();
        if (offsetHasEntry()) {
            //  Update.
            PreparedStatement update = connection.prepareStatement("UPDATE guild_settings SET value=? WHERE guild_id=? AND setting=?");
            update.setString(1, offset);
            update.setLong(2, guildId);
            update.setString(3, "offset");
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO guild_settings (guild_id,setting,value) VALUES (?,?,?)");
            insert.setLong(1, guildId);
            insert.setString(2, "offset");
            insert.setString(3, offset);
            insert.executeUpdate();
        }
        connection.close();
    }

    public boolean unsetOffset() throws SQLException {
        this.offset = Servant.config.getDefaultOffset();

        Connection connection = Database.getConnection();
        if (offsetHasEntry()) {
            //  Delete.
            PreparedStatement delete = connection.prepareStatement("DELETE FROM guild_settings WHERE guild_id=? AND setting=?");
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

    // DB
    private boolean hasEntry(String tableName, String column, String key, boolean isFeatureCount) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM "  + tableName + " WHERE " + (isFeatureCount ? "id" : "user_id") + "=? AND " + column + "=?");
        select.setLong(1, guildId);
        select.setString(2, key);
        ResultSet resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    // Feature counter.
    public int getFeatureCount(String feature) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT count FROM feature_count WHERE id=? AND feature=?");
        select.setLong(1, guildId);
        select.setString(2, feature.toLowerCase());
        ResultSet resultSet = select.executeQuery();
        int featureCount = 0;
        if (resultSet.first()) featureCount = resultSet.getInt("count");
        connection.close();
        return featureCount;
    }

    public void incrementFeatureCount(String feature) throws SQLException {
        int count = getFeatureCount(feature);
        Connection connection = Database.getConnection();
        if (hasEntry("feature_count", "feature", feature, true)) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE feature_count SET count=? WHERE id=? AND feature=?");
            update.setInt(1, count + 1);
            update.setLong(2, guildId);
            update.setString(3, feature.toLowerCase());
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO feature_count (id,feature,count) VALUES (?,?,?)");
            insert.setLong(1, guildId);
            insert.setString(2, feature.toLowerCase());
            insert.setInt(3, 1);
            insert.executeUpdate();
        }
        connection.close();
    }

    // Level
    public Map<Long, Integer> getLeaderboard() throws SQLException  {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM user_exp WHERE guild_id=? ORDER BY exp DESC");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();

        Map<Long, Integer> userExp = new LinkedHashMap<>();
        if (resultSet.first()) {
            int counter = 0;
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
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM autorole WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();

        boolean hasAutorole = false;
        if (resultSet.first()) hasAutorole = true;

        connection.close();
        return hasAutorole;
    }

    public Role getAutorole() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT role_id FROM autorole WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();

        Role role = null;
        if (resultSet.first()) {
            long roleId = resultSet.getLong("role_id");
            role = Servant.jda.getGuildById(guildId).getRoleById(roleId);
        }

        connection.close();
        return role;
    }

    private boolean autoroleHasEntry() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM autorole WHERE guild_id=?");
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

    public void setAutorole(Role role) throws SQLException {
        Connection connection = Database.getConnection();
        if (autoroleHasEntry()) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE autorole SET role_id=? WHERE guild_id=?");
            update.setLong(1, role.getIdLong());
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO autorole (guild_id,role_id) VALUES (?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, role.getIdLong());
            insert.executeUpdate();
        }
        connection.close();
    }

    public boolean unsetAutorole() throws SQLException {
        if (autoroleHasEntry()) {
            // Delete.
            Connection connection = Database.getConnection();
            PreparedStatement delete = connection.prepareStatement("DELETE FROM autorole WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
            connection.close();
            return true;
        } else return false;
    }

    // MeidaOnlyChannel
    public boolean mediaOnlyChannelHasEntry(MessageChannel channel) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM mediaonlychannel WHERE guild_id=? AND channel_id=?");
        select.setLong(1, guildId);
        select.setLong(2, channel.getIdLong());
        ResultSet resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public void addMediaOnlyChannel(MessageChannel channel) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement insert = connection.prepareStatement("INSERT INTO mediaonlychannel (guild_id,channel_id) VALUES (?,?)");
        insert.setLong(1, guildId);
        insert.setLong(2, channel.getIdLong());
        insert.executeUpdate();
        connection.close();
    }

    public boolean unsetMediaOnlyChannel(MessageChannel channel) throws SQLException {
        if (mediaOnlyChannelHasEntry(channel)) {
            Connection connection = Database.getConnection();
            PreparedStatement delete = connection.prepareStatement("DELETE FROM mediaonlychannel WHERE guild_id=? AND channel_id=?");
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
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM mediaonlychannel WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
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
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM toggle WHERE guild_id=? AND feature=?");
        select.setLong(1, guildId);
        select.setString(2, feature);
        ResultSet resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public boolean getToggleStatus(String feature) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT is_enabled FROM toggle WHERE guild_id=? AND feature=?");
        select.setLong(1, guildId);
        select.setString(2, feature);
        ResultSet resultSet = select.executeQuery();
        boolean isEnabled;
        if (resultSet.first()) isEnabled = resultSet.getBoolean("is_enabled");
        else isEnabled = Servant.toggle.get(feature);

        connection.close();
        return isEnabled;
    }

    public void setToggleStatus(String feature, boolean status) throws SQLException {
        Connection connection = Database.getConnection();
        if (toggleHasEntry(feature)) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE toggle SET is_enabled=? WHERE guild_id=? AND feature=?");
            update.setBoolean(1, status);
            update.setLong(2, guildId);
            update.setString(3, feature);
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO toggle (guild_id,feature,is_enabled) VALUES (?,?,?)");
            insert.setLong(1, guildId);
            insert.setString(2, feature);
            insert.setBoolean(3, status);
            insert.executeUpdate();
        }
        connection.close();
    }

    // JoinNotifier
    private boolean joinNotifierHasEntry() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM join_notifier WHERE guild_id=?");
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

    public MessageChannel getJoinNotifierChannel() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT channel_id FROM join_notifier WHERE guild_id=?");
        select.setLong(1, guildId);
        ResultSet resultSet = select.executeQuery();
        MessageChannel channel = null;
        if (resultSet.first()) channel = Servant.jda.getGuildById(guildId).getTextChannelById(resultSet.getLong("channel_id"));

        connection.close();
        return channel;
    }

    public void setJoinNotifierChannel(MessageChannel channel) throws SQLException {
        Connection connection = Database.getConnection();
        if (joinNotifierHasEntry()) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE join_notifier SET channel_id=? WHERE guild_id=?");
            update.setLong(1, channel.getIdLong());
            update.setLong(2, guildId);
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO join_notifier (guild_id,channel_id) VALUES (?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, channel.getIdLong());
            insert.executeUpdate();
        }
        connection.close();
    }

    public boolean unsetJoinNotifierChannel() throws SQLException {
        if (joinNotifierHasEntry()) {
            Connection connection = Database.getConnection();
            PreparedStatement delete = connection.prepareStatement("DELETE FROM join_notifier WHERE guild_id=?");
            delete.setLong(1, guildId);
            delete.executeUpdate();
            connection.close();
            return true;
        } else {
            return false;
        }
    }
}
