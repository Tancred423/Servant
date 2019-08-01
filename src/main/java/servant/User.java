// Author: Tancred423 (https://github.com/Tancred423)
package servant;

import patreon.PatreonHandler;

import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class User {
    private long userId;

    public User(long userId) {
        this.userId = userId;
    }

    // Color.
    public String getColorCode() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT value FROM user_settings WHERE user_id=? AND setting=?");
        select.setLong(1, userId);
        select.setString(2, "color");
        var resultSet = select.executeQuery();
        String colorCode;
        if (resultSet.first()) colorCode = resultSet.getString("value");
        else {
            var user = Servant.jda.getUserById(userId);
            if (PatreonHandler.is$10Patron(user)) colorCode = "#29b6f6";
            else if (PatreonHandler.is$5Patron(user)) colorCode = "#01ca9e";
            else if (PatreonHandler.is$3Patron(user)) colorCode = "#ffca28";
            else if (PatreonHandler.is$1Patron(user)) colorCode = "#bebebe";
            else if (PatreonHandler.isDonator(user)) colorCode = "#cd7f32";
            else colorCode = Servant.config.getDefaultColorCode();
        }
        connection.close();
        return colorCode;
    }

    // Color.
    public Color getColor() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT value FROM user_settings WHERE user_id=? AND setting=?");
        select.setLong(1, userId);
        select.setString(2, "color");
        var resultSet = select.executeQuery();
        Color color;
        if (resultSet.first()) color = Color.decode(resultSet.getString("value"));
        else {
            var user = Servant.jda.getUserById(userId);
            var guild = Servant.jda.getGuildById(436925371577925642L);
            if (PatreonHandler.is$10Patron(user)) color = guild.getRoleById(502472869234868224L).getColor();
            else if (PatreonHandler.is$5Patron(user)) color = guild.getRoleById(502472823638458380L).getColor();
            else if (PatreonHandler.is$3Patron(user)) color = guild.getRoleById(502472546600353796L).getColor();
            else if (PatreonHandler.is$1Patron(user)) color = guild.getRoleById(502472440455233547L).getColor();
            else if (PatreonHandler.isDonator(user)) color = guild.getRoleById(489738762838867969L).getColor();
            else color = Color.decode(Servant.config.getDefaultColorCode());
        }
        connection.close();
        return color;
    }

    public void setColor(String colorCode) throws SQLException {
        var connection = Database.getConnection();
        if (hasEntry("user_settings", "setting", "color", false)) {
            //  Update.
            var update = connection.prepareStatement("UPDATE user_settings SET value=? WHERE user_id=? AND setting=?");
            update.setString(1, colorCode);
            update.setLong(2, userId);
            update.setString(3, "color");
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO user_settings (user_id,setting,value) VALUES (?,?,?)");
            insert.setLong(1, userId);
            insert.setString(2, "color");
            insert.setString(3, colorCode);
            insert.executeUpdate();
        }
        connection.close();
    }

    public boolean unsetColor() throws SQLException {
        var connection = Database.getConnection();
        if (hasEntry("user_settings", "setting", "color", false)) {
            //  Delete.
            var delete = connection.prepareStatement("DELETE FROM user_settings WHERE user_id=? AND setting=?");
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
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM "  + tableName + " WHERE " + (isFeatureCount ? "id" : "user_id") + "=? AND " + column + "=?");
        select.setLong(1, userId);
        select.setString(2, key);
        var resultSet = select.executeQuery();
        return resultSet.first();
    }

    // Interaction.
    public int getInteractionCount(String interaction, boolean isShared) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT " + (isShared ? "shared" : "received") + " FROM interaction_count WHERE user_id=? AND interaction=?");
        select.setLong(1, userId);
        select.setString(2, interaction.toLowerCase());
        var resultSet = select.executeQuery();
        int commandCount = 0;
        if (resultSet.first()) commandCount = resultSet.getInt(isShared ? "shared" : "received");
        connection.close();
        return commandCount;
    }

    public void incrementInteractionCount(String interaction, boolean isShared) throws SQLException {
        var count = getInteractionCount(interaction, isShared);
        var connection = Database.getConnection();
        if (hasEntry("interaction_count", "interaction", interaction, false)) {
            // Update.
            var update = connection.prepareStatement("UPDATE interaction_count SET " + (isShared ? "shared" : "received") + "=? WHERE user_id=? AND interaction=?");
            update.setInt(1, count + 1);
            update.setLong(2, userId);
            update.setString(3, interaction.toLowerCase());
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO interaction_count (user_id,interaction,shared,received) VALUES (?,?,?,?)");
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
    private int getFeatureCount(String feature) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT count FROM feature_count WHERE id=? AND feature=?");
        select.setLong(1, userId);
        select.setString(2, feature.toLowerCase());
        var resultSet = select.executeQuery();
        var featureCount = 0;
        if (resultSet.first()) featureCount = resultSet.getInt("count");
        connection.close();
        return featureCount;
    }

    public void incrementFeatureCount(String feature) throws SQLException {
        var count = getFeatureCount(feature);
        var connection = Database.getConnection();
        if (hasEntry("feature_count", "feature", feature, true)) {
            // Update.
            var update = connection.prepareStatement("UPDATE feature_count SET count=? WHERE id=? AND feature=?");
            update.setInt(1, count + 1);
            update.setLong(2, userId);
            update.setString(3, feature.toLowerCase());
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO feature_count (id,feature,count) VALUES (?,?,?)");
            insert.setLong(1, userId);
            insert.setString(2, feature.toLowerCase());
            insert.setInt(3, 1);
            insert.executeUpdate();
        }
        connection.close();
    }

    // Level
    public int getExp(long guildId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM user_exp WHERE user_id=? AND guild_id=?");
        select.setLong(1, userId);
        select.setLong(2, guildId);
        var resultSet = select.executeQuery();
        var exp = 0;
        if (resultSet.first()) exp = resultSet.getInt("exp");
        connection.close();
        return exp;
    }

    public void addExp(long guildId, int exp) throws SQLException {
        var connection = Database.getConnection();
        if (getExp(guildId) == 0) {
            // Entry does not exist yet.
            var insert = connection.prepareStatement("INSERT INTO user_exp(user_id,guild_id,exp) VALUES(?,?,?)");
            insert.setLong(1, userId);
            insert.setLong(2, guildId);
            insert.setInt(3, exp);
            insert.executeUpdate();
        } else {
            // Entry already existed.
            var currentExp = getExp(guildId);
            exp += currentExp;
            var update = connection.prepareStatement("UPDATE user_exp SET exp=? WHERE user_id=? AND guild_id=?");
            update.setInt(1, exp);
            update.setLong(2, userId);
            update.setLong(3, guildId);
            update.executeUpdate();
        }
        connection.close();
    }

    public Map<String, Integer> getMostUsedFeature() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM feature_count WHERE id=? ORDER BY count DESC");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        Map<String, Integer> feature = new HashMap<>();
        if (resultSet.first()) feature.put(resultSet.getString("feature"), resultSet.getInt("count"));
        else feature.put("Not found.", 0);
        connection.close();
        return feature;
    }
}
