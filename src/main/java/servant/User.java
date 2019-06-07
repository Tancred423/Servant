package servant;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private long userId;
    private Color color;

    public User(long userId) throws SQLException {
        this.userId = userId;
        thisColor();
    }

    // Color.
    public Color getColor() { return color; }

    private void thisColor() throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT value FROM user_settings WHERE user_id=? AND setting=?");
        select.setLong(1, userId);
        select.setString(2, "color");
        ResultSet resultSet = select.executeQuery();
        String colorCode = null;
        if (resultSet.first()) colorCode = resultSet.getString("value");
        if (colorCode == null) color = Color.decode(Servant.config.getDefaultColorCode());
        else color = Color.decode(colorCode);
        connection.close();
    }

    public void setColor(String colorCode) throws SQLException {
        this.color = Color.decode(colorCode);

        Connection connection = Database.getConnection();
        if (hasEntry("user_settings", "setting", "color", false)) {
            //  Update.
            PreparedStatement update = connection.prepareStatement("UPDATE user_settings SET value=? WHERE user_id=? AND setting=?");
            update.setString(1, colorCode);
            update.setLong(2, userId);
            update.setString(3, "color");
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO user_settings (user_id,setting,value) VALUES (?,?,?)");
            insert.setLong(1, userId);
            insert.setString(2, "color");
            insert.setString(3, colorCode);
            insert.executeUpdate();
        }
        connection.close();
    }

    public boolean unsetColor() throws SQLException {
        this.color = Color.decode(Servant.config.getDefaultColorCode());

        Connection connection = Database.getConnection();
        if (hasEntry("user_settings", "setting", "color", false)) {
            //  Delete.
            PreparedStatement delete = connection.prepareStatement("DELETE FROM user_settings WHERE user_id=? AND setting=?");
            delete.setLong(1, userId);
            delete.setString(2, "color");
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
        select.setLong(1, userId);
        select.setString(2, key);
        ResultSet resultSet = select.executeQuery();
        return resultSet.first();
    }

    // Interaction.
    public int getInteractionCount(String interaction, boolean isShared) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT " + (isShared ? "shared" : "received") + " FROM interaction_count WHERE user_id=? AND interaction=?");
        select.setLong(1, userId);
        select.setString(2, interaction.toLowerCase());
        ResultSet resultSet = select.executeQuery();
        int commandCount = 0;
        if (resultSet.first()) commandCount = resultSet.getInt(isShared ? "shared" : "received");
        connection.close();
        return commandCount;
    }

    public void incrementInteractionCount(String interaction, boolean isShared) throws SQLException {
        int count = getInteractionCount(interaction, isShared);
        Connection connection = Database.getConnection();
        if (hasEntry("interaction_count", "interaction", interaction, false)) {
            // Update.
            PreparedStatement update = connection.prepareStatement("UPDATE interaction_count SET " + (isShared ? "shared" : "received") + "=? WHERE user_id=? AND interaction=?");
            update.setInt(1, count + 1);
            update.setLong(2, userId);
            update.setString(3, interaction.toLowerCase());
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO interaction_count (user_id,interaction,shared,received) VALUES (?,?,?,?)");
            insert.setLong(1, userId);
            insert.setString(2, interaction.toLowerCase());
            if (isShared) {
                insert.setInt(3, 1);
                insert.setInt(4, 0);
            } else {
                insert.setInt(3, 0);
                insert.setInt(4, 1);
            }
            insert.executeUpdate();
        }
        connection.close();
    }

    // Feature counter.
    public int getFeatureCount(String feature) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT count FROM feature_count WHERE id=? AND feature=?");
        select.setLong(1, userId);
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
            update.setLong(2, userId);
            update.setString(3, feature.toLowerCase());
            update.executeUpdate();
        } else {
            // Insert.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO feature_count (id,feature,count) VALUES (?,?,?)");
            insert.setLong(1, userId);
            insert.setString(2, feature.toLowerCase());
            insert.setInt(3, 1);
            insert.executeUpdate();
        }
        connection.close();
    }

    // Level
    public int getExp(long guildId) throws SQLException {
        Connection connection = Database.getConnection();
        PreparedStatement select = connection.prepareStatement("SELECT * FROM user_exp WHERE user_id=? AND guild_id=?");
        select.setLong(1, userId);
        select.setLong(2, guildId);
        ResultSet resultSet = select.executeQuery();
        int exp = 0;
        if (resultSet.first()) exp = resultSet.getInt("exp");
        connection.close();
        return exp;
    }

    public void addExp(long guildId, int exp) throws SQLException {
        Connection connection = Database.getConnection();
        if (getExp(guildId) == 0) {
            // Entry does not exist yet.
            PreparedStatement insert = connection.prepareStatement("INSERT INTO user_exp(user_id,guild_id,exp) VALUES(?,?,?)");
            insert.setLong(1, userId);
            insert.setLong(2, guildId);
            insert.setInt(3, exp);
            insert.executeUpdate();
        } else {
            // Entry already existed.
            int currentExp = getExp(guildId);
            exp += currentExp;
            PreparedStatement update = connection.prepareStatement("UPDATE user_exp SET exp=? WHERE user_id=? AND guild_id=?");
            update.setInt(1, exp);
            update.setLong(2, userId);
            update.setLong(3, guildId);
            update.executeUpdate();
        }
        connection.close();
    }
}
