// Author: Tancred423 (https://github.com/Tancred423)
package moderation.user;

import patreon.PatreonHandler;
import servant.Database;
import servant.Servant;

import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class User {
    private long userId;

    public User(long userId) {
        this.userId = userId;
    }

    // Offset
    public String getBio() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT text FROM bio WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        var text = "";
        if (resultSet.first()) text = resultSet.getString("text");
        connection.close();
        return text;
    }

    private boolean bioHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT text FROM bio WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public void setBio(String text) throws SQLException {
        var connection = Database.getConnection();
        if (bioHasEntry()) {
            //  Update.
            var update = connection.prepareStatement("UPDATE bio SET text=? WHERE user_id=?");
            update.setString(1, text);
            update.setLong(2, userId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO bio (user_id,text) VALUES (?,?)");
            insert.setLong(1, userId);
            insert.setString(2, text);
            insert.executeUpdate();
        }
        connection.close();
    }

    // Baguette
    public Map<Integer, Integer> getBaguette() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM baguette_counter WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        var baguette = new HashMap<Integer, Integer>();
        if (resultSet.first()) baguette.put(resultSet.getInt("baguette_size"), resultSet.getInt("size_counter"));
        connection.close();
        return baguette;
    }

    private boolean baguetteHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM baguette_counter WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public void setBaguette(int baguetteSize, int sizeCounter) throws SQLException {
        var connection = Database.getConnection();
        if (baguetteHasEntry()) {
            //  Update.
            var update = connection.prepareStatement("UPDATE baguette_counter SET baguette_size=?, size_counter=? WHERE user_id=?");
            update.setInt(1, baguetteSize);
            update.setInt(2, sizeCounter);
            update.setLong(3, userId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO baguette_counter (user_id,baguette_size,size_counter) VALUES (?,?,?)");
            insert.setLong(1, userId);
            insert.setInt(2, baguetteSize);
            insert.setInt(3, sizeCounter);
            insert.executeUpdate();
        }
        connection.close();
    }

    // Achievement
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasAchievement(String achievement) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM achievement WHERE user_id=? AND achievement=?");
        select.setLong(1, userId);
        select.setString(2, achievement.toLowerCase());
        var resultSet = select.executeQuery();
        boolean hasAchievement = false;
        if (resultSet.first()) hasAchievement = true;
        connection.close();
        return hasAchievement;
    }

    public Map<String, Integer> getAchievements() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM achievement WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        Map<String, Integer> achievements = new HashMap<>();
        if (resultSet.first())
            do achievements.put(resultSet.getString("achievement"), resultSet.getInt("ap"));
            while (resultSet.next());
        connection.close();
        return achievements;
    }

    public int getTotelAP() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM achievement WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        var ap = 0;
        if (resultSet.first()) do ap += resultSet.getInt("ap"); while (resultSet.next());
        connection.close();
        return ap;
    }

    public void setAchievement(String achievement, int ap) throws SQLException {
        var connection = Database.getConnection();
        var insert = connection.prepareStatement("INSERT INTO achievement (user_id,achievement,ap) VALUES (?,?,?)");
        insert.setLong(1, userId);
        insert.setString(2, achievement.toLowerCase());
        insert.setInt(3, ap);
        insert.executeUpdate();
        connection.close();
    }

    // Offset
    public String getOffset() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT offset FROM user WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        var offset = Servant.config.getDefaultOffset();
        if (resultSet.first()) offset = resultSet.getString("offset");
        connection.close();
        return offset;
    }

    private boolean offsetHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT offset FROM user WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public void setOffset(String offset) throws SQLException {
        var connection = Database.getConnection();
        if (offsetHasEntry()) {
            //  Update.
            var update = connection.prepareStatement("UPDATE user SET offset=? WHERE user_id=?");
            update.setString(1, offset);
            update.setLong(2, userId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO user (user_id,offset,prefix,language) VALUES (?,?,?,?)");
            insert.setLong(1, userId);
            insert.setString(2, offset);
            insert.setString(3, Servant.config.getDefaultPrefix());
            insert.setString(4, Servant.config.getDefaultLanguage());
            insert.executeUpdate();
        }
        connection.close();
    }

    void unsetOffset() throws SQLException {
        setOffset(Servant.config.getDefaultOffset());
    }

    // Prefix
    public String getPrefix() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT prefix FROM user WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        String prefix = Servant.config.getDefaultPrefix();
        if (resultSet.first()) prefix = resultSet.getString("prefix");
        connection.close();
        return prefix;
    }

    private boolean prefixHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT prefix FROM user WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public void setPrefix(String prefix) throws SQLException {
        var connection = Database.getConnection();
        if (prefixHasEntry()) {
            //  Update.
            var update = connection.prepareStatement("UPDATE user SET prefix=? WHERE user_id=?");
            update.setString(1, prefix);
            update.setLong(2, userId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO user (user_id,offset,prefix,language) VALUES (?,?,?,?)");
            insert.setLong(1, userId);
            insert.setString(2, Servant.config.getDefaultOffset());
            insert.setString(3, prefix);
            insert.setString(4, Servant.config.getDefaultLanguage());
            insert.executeUpdate();
        }
        connection.close();
    }

    void unsetPrefix() throws SQLException {
        setPrefix(Servant.config.getDefaultPrefix());
    }

    // Language
    public String getLanguage() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT language FROM user WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        String language = null;
        if (resultSet.first()) language = resultSet.getString("language");
        if (language == null) language = Servant.config.getDefaultLanguage();
        else if (language.isEmpty()) language = Servant.config.getDefaultLanguage();
        connection.close();
        return language;
    }

    private boolean languageHasEntry() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT language FROM user WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        if (resultSet.first()) {
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public void setLanguage(String language) throws SQLException {
        var connection = Database.getConnection();
        if (languageHasEntry()) {
            //  Update.
            var update = connection.prepareStatement("UPDATE user SET language=? WHERE user_id=?");
            update.setString(1, language);
            update.setLong(2, userId);
            update.executeUpdate();
        } else {
            // Insert.
            var insert = connection.prepareStatement("INSERT INTO user (user_id,offset,prefix,language) VALUES (?,?,?,?)");
            insert.setLong(1, userId);
            insert.setString(2, Servant.config.getDefaultOffset());
            insert.setString(3, Servant.config.getDefaultPrefix());
            insert.setString(4, language);
            insert.executeUpdate();
        }
        connection.close();
    }

    void unsetLanguage() throws SQLException {
        setLanguage(Servant.config.getDefaultLanguage());
    }

    // Stream Hidden
    public boolean isStreamHidden(long guildId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM streamhidden WHERE guild_id=? AND user_id=?");
        select.setLong(1, userId);
        select.setLong(2, guildId);
        var resultSet = select.executeQuery();
        boolean isStreamHiddden = false;
        if (resultSet.first()) isStreamHiddden = true;
        connection.close();
        return isStreamHiddden;
    }

    List<Long> getStreamHiddenGuilds() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM streamhidden WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        List<Long> streamHiddenGuilds = new ArrayList<>();
        if (resultSet.first()) do streamHiddenGuilds.add(resultSet.getLong("guild_id")); while (resultSet.next());
        connection.close();
        return streamHiddenGuilds;
    }

    boolean toggleStreamHidden(long guildId) throws SQLException {
        if (!isStreamHidden(guildId)) {
            var connection = Database.getConnection();
            var insert = connection.prepareStatement("INSERT INTO streamhidden (guild_id,user_id) VALUES (?,?)");
            insert.setLong(1, guildId);
            insert.setLong(2, userId);
            insert.executeUpdate();
            connection.close();
            return true;
        } else {
            unsetStreamHiden(guildId);
            return false;
        }
    }

    private void unsetStreamHiden(long guildId) throws SQLException {
        var connection = Database.getConnection();
        var delete = connection.prepareStatement("DELETE FROM streamhidden WHERE guild_id=? AND user_id=?");
        delete.setLong(1, guildId);
        delete.setLong(2, userId);
        delete.executeUpdate();
        connection.close();
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
            var guild = Servant.jda.getGuildById(436925371577925642L);
            Color color = null;
            if (PatreonHandler.is$10Patron(user)) color = guild.getRoleById(502472869234868224L).getColor();
            else if (PatreonHandler.is$5Patron(user)) color = guild.getRoleById(502472823638458380L).getColor();
            else if (PatreonHandler.is$3Patron(user)) color = guild.getRoleById(502472546600353796L).getColor();
            else if (PatreonHandler.is$1Patron(user)) color = guild.getRoleById(502472440455233547L).getColor();
            else if (PatreonHandler.isDonator(user)) color = guild.getRoleById(489738762838867969L).getColor();

            if (color != null)
                colorCode = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            else colorCode = Servant.config.getDefaultColorCode();
        }
        connection.close();
        return colorCode;
    }

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

    boolean unsetColor() throws SQLException {
        if (hasEntry("user_settings", "setting", "color", false)) {
            var connection = Database.getConnection();
            var delete = connection.prepareStatement("DELETE FROM user_settings WHERE user_id=? AND setting=?");
            delete.setLong(1, userId);
            delete.setString(2, "color");
            delete.executeUpdate();
            connection.close();
            return true;
        } else return false;
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

    public Map<String, Integer> getTop10MostUsedFeatures() throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM feature_count WHERE id=? ORDER BY count DESC");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        var feature = new LinkedHashMap<String, Integer>();
        var counter = 0;
        if (resultSet.first())
            do {
                feature.put(resultSet.getString("feature"), resultSet.getInt("count"));
                if (counter < 9) counter++;
                else break;
            } while (resultSet.next());
        connection.close();
        return feature;
    }
}
