// Author: Tancred423 (https://github.com/Tancred423)
package moderation.user;

import files.language.LanguageHandler;
import interaction.Interaction;
import net.dv8tion.jda.api.entities.User;
import servant.Log;
import servant.Servant;
import useful.remindme.RemindMe;
import utilities.MyEntry;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static servant.Database.closeQuietly;

public class Master {
    private User user;
    private long userId;

    public Master(User user) {
        this.user = user;
        this.userId = user.getIdLong();
    }

    public User getUser() {
        return user;
    }

    public long getUserId() {
        return userId;
    }

    // Methods
    // RemindMe
    public RemindMe getRemindMe(int aiNumber) {
        Connection connection = null;
        RemindMe remindMe = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM remindme WHERE ai_number=? AND user_id=?");
            select.setInt(1, aiNumber);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                remindMe = new RemindMe(aiNumber, userId, resultSet.getTimestamp("event_time"), resultSet.getString("topic"));
        } catch (SQLException e) {
            new Log(e, null, null, "RemindMes.java", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return remindMe;
    }

    public int setRemindMe(Timestamp eventTime, String topic) {
        Connection connection = null;
        var aiNumber = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("INSERT INTO remindme (user_id,event_time,topic) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            insert.setLong(1, userId);
            insert.setTimestamp(2, eventTime);
            insert.setString(3, topic);
            insert.executeUpdate();

            var resultSet = insert.getGeneratedKeys();
            if (resultSet.first()) aiNumber = resultSet.getInt(1);
        } catch (SQLException e) {
            new Log(e, null, user, "Master.java - setRemindMe(Timestamp, String)", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return aiNumber;
    }

    public void unsetRemindMe(int aiNumber) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("DELETE FROM remindme WHERE ai_number=? AND user_id=?");
            insert.setInt(1, aiNumber);
            insert.setLong(2, userId);
            insert.executeUpdate();
        } catch (SQLException e) {
            new Log(e, null, user, "Master.java - unsetRemindMe(int, long)", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

    }

    // Reminder
    public boolean setReminder(Timestamp reminderTime, String topic) {
        Connection connection = null;
        var wasSet = false;

        try {
            if (!reminderHasEntry(reminderTime)) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO reminder (user_id,reminder_time,topic) VALUES (?,?,?)");
                insert.setLong(1, userId);
                insert.setTimestamp(2, reminderTime);
                insert.setString(3, topic);
                insert.executeUpdate();
                wasSet = true;
            }
        } catch (SQLException e) {
            new Log(e, null, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasSet;
    }

    public void unsetReminder(Timestamp reminderTime) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("DELETE FROM reminder WHERE user_id=? AND reminder_time=?");
            insert.setLong(1, userId);
            insert.setTimestamp(2, reminderTime);
            insert.executeUpdate();
        } catch (SQLException e) {
            new Log(e, null, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    private boolean reminderHasEntry(Timestamp reminderTime) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM reminder WHERE user_id=? AND reminder_time=?");
            select.setLong(1, userId);
            select.setTimestamp(2, reminderTime);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, null, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public Map<Timestamp, String> getReminders() {
        Connection connection = null;
        var reminders = new HashMap<Timestamp, String>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM reminder WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do reminders.put(resultSet.getTimestamp("reminder_time"), resultSet.getString("topic"));
                while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, null, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return reminders;
    }

    // Bio
    public String getBio() {
        Connection connection = null;
        var text = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT text FROM bio WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) text = resultSet.getString("text");
        } catch (SQLException e) {
            new Log(e, null, user, "bio", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return text;
    }

    private boolean bioHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT text FROM bio WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, null, user, "bio", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void setBio(String text) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bioHasEntry()) {
                var update = connection.prepareStatement("UPDATE bio SET text=? WHERE user_id=?");
                update.setString(1, text);
                update.setLong(2, userId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO bio (user_id,text) VALUES (?,?)");
                insert.setLong(1, userId);
                insert.setString(2, text);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, null, user, "bio", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Baguette
    public MyEntry<Integer, Integer> getBaguette() {
        Connection connection = null;
        MyEntry<Integer, Integer> baguette = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM baguette_counter WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                baguette = new MyEntry<>(resultSet.getInt("baguette_size"), resultSet.getInt("size_counter"));
        } catch (SQLException e) {
            new Log(e, null, user, "baguette", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return baguette;
    }

    private boolean baguetteHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM baguette_counter WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, null, user, "baguette", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void setBaguette(int baguetteSize, int sizeCounter) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (baguetteHasEntry()) {
                var update = connection.prepareStatement("UPDATE baguette_counter SET baguette_size=?, size_counter=? WHERE user_id=?");
                update.setInt(1, baguetteSize);
                update.setInt(2, sizeCounter);
                update.setLong(3, userId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO baguette_counter (user_id,baguette_size,size_counter) VALUES (?,?,?)");
                insert.setLong(1, userId);
                insert.setInt(2, baguetteSize);
                insert.setInt(3, sizeCounter);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, null, user, "baguette", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Achievement
    public boolean hasAchievement(String achievement) {
        Connection connection = null;
        var hasAchievement = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM achievement WHERE user_id=? AND achievement=?");
            select.setLong(1, userId);
            select.setString(2, achievement.toLowerCase());
            var resultSet = select.executeQuery();
            hasAchievement = resultSet.first();
        } catch (SQLException e) {
            new Log(e, null, user, "achievement", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasAchievement;
    }

    public Map<String, Integer> getAchievements() {
        Connection connection = null;
        var achievements = new TreeMap<String, Integer>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM achievement WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do achievements.put(resultSet.getString("achievement"), resultSet.getInt("ap"));
                while (resultSet.next());
            }
        } catch (SQLException e) {
            new Log(e, null, user, "achievement", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        // Sort By Value
        return achievements.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public List<String> getLevelAchievements() {
        Connection connection = null;
        var achievements = new LinkedList<String>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT achievement FROM achievement WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do {
                    var achievement = resultSet.getString("achievement");
                    if (achievement.startsWith("level")) achievements.add(achievement);
                } while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, null, user, "achievement", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return achievements;
    }

    public int getTotalAP() {
        Connection connection = null;
        var ap = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM achievement WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) do ap += resultSet.getInt("ap"); while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, null, user, "achievement", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return ap;
    }

    public void setAchievement(String achievement, int ap) {
        Connection connection = null;

        try {
            if (!hasAchievement(achievement)) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO achievement (user_id,achievement,ap) VALUES (?,?,?)");
                insert.setLong(1, userId);
                insert.setString(2, achievement.toLowerCase());
                insert.setInt(3, ap);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, null, user, "achievement", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetAchievement(String achievement) {
        Connection connection = null;

        try {
            if (hasAchievement(achievement)) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM achievement WHERE user_id=? AND achievement=?");
                delete.setLong(1, userId);
                delete.setString(2, achievement.toLowerCase());
                delete.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, null, user, "achievement", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Offset
    public String getOffset() {
        Connection connection = null;
        var offset = Servant.config.getDefaultOffset();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT offset FROM user WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) offset = resultSet.getString("offset");
            offset = offset.equals("00:00") ? "Z" : offset;
        } catch (SQLException e) {
            new Log(e, null, user, "offset", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return offset;
    }

    private boolean offsetHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT offset FROM user WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, null, user, "offset", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    void setOffset(String offset) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (offsetHasEntry()) {
                var update = connection.prepareStatement("UPDATE user SET offset=? WHERE user_id=?");
                update.setString(1, offset);
                update.setLong(2, userId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO user (user_id,offset,prefix,language) VALUES (?,?,?,?)");
                insert.setLong(1, userId);
                insert.setString(2, offset);
                insert.setString(3, Servant.config.getDefaultPrefix());
                insert.setString(4, Servant.config.getDefaultLanguage());
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, null, user, "offset", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    void unsetOffset() {
        setOffset(Servant.config.getDefaultOffset());
    }

    // Prefix
    public String getPrefix() {
        Connection connection = null;
        String prefix = Servant.config.getDefaultPrefix();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT prefix FROM user WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) prefix = resultSet.getString("prefix");
        } catch (SQLException e) {
            new Log(e, null, user, "prefix", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return prefix;
    }

    private boolean prefixHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT prefix FROM user WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, null, user, "prefix", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    void setPrefix(String prefix) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (prefixHasEntry()) {
                var update = connection.prepareStatement("UPDATE user SET prefix=? WHERE user_id=?");
                update.setString(1, prefix);
                update.setLong(2, userId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO user (user_id,offset,prefix,language) VALUES (?,?,?,?)");
                insert.setLong(1, userId);
                insert.setString(2, Servant.config.getDefaultOffset());
                insert.setString(3, prefix);
                insert.setString(4, Servant.config.getDefaultLanguage());
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, null, user, "prefix", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    void unsetPrefix() {
        setPrefix(Servant.config.getDefaultPrefix());
    }

    // Language
    public String getLanguage() {
        Connection connection = null;
        String language = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT language FROM user WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) language = resultSet.getString("language");
            if (language == null) language = Servant.config.getDefaultLanguage();
            else if (language.isEmpty()) language = Servant.config.getDefaultLanguage();
        } catch (SQLException e) {
            new Log(e, null, user, "language", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return language;
    }

    private boolean languageHasEntry() {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT language FROM user WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, null, user, "language", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    void setLanguage(String language) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (languageHasEntry()) {
                var update = connection.prepareStatement("UPDATE user SET language=? WHERE user_id=?");
                update.setString(1, language);
                update.setLong(2, userId);
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO user (user_id,offset,prefix,language) VALUES (?,?,?,?)");
                insert.setLong(1, userId);
                insert.setString(2, Servant.config.getDefaultOffset());
                insert.setString(3, Servant.config.getDefaultPrefix());
                insert.setString(4, language);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, null, user, "language", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    void unsetLanguage() {
        setLanguage(Servant.config.getDefaultLanguage());
    }

    // Stream Hidden
    public boolean isStreamHidden(long guildId) {
        Connection connection = null;
        var isStreamHidden = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM streamhidden WHERE guild_id=? AND user_id=?");
            select.setLong(1, guildId);
            select.setLong(2, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) isStreamHidden = true;
        } catch (SQLException e) {
            new Log(e, null, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isStreamHidden;
    }

    public List<Long> getStreamHiddenGuilds() {
        Connection connection = null;
        var streamHiddenGuilds = new ArrayList<Long>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM streamhidden WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) do streamHiddenGuilds.add(resultSet.getLong("guild_id")); while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, null, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return streamHiddenGuilds;
    }

    boolean toggleStreamHidden(long guildId) {
        Connection connection = null;
        var isStreamHidden = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (!isStreamHidden(guildId)) {
                var insert = connection.prepareStatement("INSERT INTO streamhidden (guild_id,user_id) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, userId);
                insert.executeUpdate();
                isStreamHidden = true;
            } else {
                unsetStreamHidden(guildId);
                isStreamHidden = false;
            }
        } catch (SQLException e) {
            new Log(e, null, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isStreamHidden;
    }

    private void unsetStreamHidden(long guildId) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM streamhidden WHERE guild_id=? AND user_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, userId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new Log(e, null, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Color
    public String getColorCode() {
        Connection connection = null;
        var colorCode = Servant.config.getDefaultColorCode();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT value FROM user_settings WHERE user_id=? AND setting=?");
            select.setLong(1, userId);
            select.setString(2, "color");
            var resultSet = select.executeQuery();
            if (resultSet.first()) colorCode = resultSet.getString("value");
            else {
                var servantsKingdom = user.getJDA().getGuildById(436925371577925642L);
                if (servantsKingdom == null) return colorCode;
                Color color = null;
                if (isCreator()) {
                    colorCode = Servant.config.getBotOwnerColorCode();
                } else if (isVIP()) {
                    var role = servantsKingdom.getRoleById(510204269568458753L);
                    if (role != null) color = role.getColor();
                } else if (is$10Patron()) {
                    var role = servantsKingdom.getRoleById(502472869234868224L);
                    if (role != null) color = role.getColor();
                } else if (is$5Patron()) {
                    var role = servantsKingdom.getRoleById(502472823638458380L);
                    if (role != null) color = role.getColor();
                } else if (is$3Patron()) {
                    var role = servantsKingdom.getRoleById(502472546600353796L);
                    if (role != null) color = role.getColor();
                } else if (is$1Patron()) {
                    var role = servantsKingdom.getRoleById(502472440455233547L);
                    if (role != null) color = role.getColor();
                } else if (isDonator()) {
                    var role = servantsKingdom.getRoleById(489738762838867969L);
                    if (role != null) color = role.getColor();
                } else if (isServerBooster()) {
                    var role = servantsKingdom.getRoleById(639128857747652648L);
                    if (role != null) color = role.getColor();
                }

                if (color != null) colorCode = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            }
        } catch (SQLException e) {
            new Log(e, null, user, "color", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return colorCode;
    }

    public Color getColor() {
        Connection connection = null;
        var color = Color.decode(Servant.config.getDefaultColorCode());

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT value FROM user_settings WHERE user_id=? AND setting=?");
            select.setLong(1, userId);
            select.setString(2, "color");
            var resultSet = select.executeQuery();
            if (resultSet.first()) color = Color.decode(resultSet.getString("value"));
            else {
                var servantsKingdom = user.getJDA().getGuildById(436925371577925642L);
                if (servantsKingdom == null) return color;
                if (isCreator()) {
                    color = Color.decode(Servant.config.getBotOwnerColorCode());
                } else if (isVIP()) {
                    var role = servantsKingdom.getRoleById(510204269568458753L);
                    if (role != null) color = role.getColor();
                } else if (is$10Patron()) {
                    var role = servantsKingdom.getRoleById(502472869234868224L);
                    if (role != null) color = role.getColor();
                } else if (is$5Patron()) {
                    var role = servantsKingdom.getRoleById(502472823638458380L);
                    if (role != null) color = role.getColor();
                } else if (is$3Patron()) {
                    var role = servantsKingdom.getRoleById(502472546600353796L);
                    if (role != null) color = role.getColor();
                } else if (is$1Patron()) {
                    var role = servantsKingdom.getRoleById(502472440455233547L);
                    if (role != null) color = role.getColor();
                } else if (isDonator()) {
                    var role = servantsKingdom.getRoleById(489738762838867969L);
                    if (role != null) color = role.getColor();
                } else if (isServerBooster()) {
                    var role = servantsKingdom.getRoleById(639128857747652648L);
                    if (role != null) color = role.getColor();
                }
            }
        } catch (SQLException e) {
            new Log(e, null, user, "color", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return color;
    }

    public void setColor(String colorCode) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (hasEntry("user_settings", "setting", "color", false)) {
                var update = connection.prepareStatement("UPDATE user_settings SET value=? WHERE user_id=? AND setting=?");
                update.setString(1, colorCode);
                update.setLong(2, userId);
                update.setString(3, "color");
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO user_settings (user_id,setting,value) VALUES (?,?,?)");
                insert.setLong(1, userId);
                insert.setString(2, "color");
                insert.setString(3, colorCode);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, null, user, "color", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    boolean unsetColor() {
        Connection connection = null;
        var wasUnset = false;

        try {
            if (hasEntry("user_settings", "setting", "color", false)) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM user_settings WHERE user_id=? AND setting=?");
                delete.setLong(1, userId);
                delete.setString(2, "color");
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            new Log(e, null, user, "color", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    // DB
    private boolean hasEntry(String tableName, String column, String key, boolean isFeatureCount) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM "  + tableName + " WHERE " + (isFeatureCount ? "id" : "user_id") + "=? AND " + column + "=?");
            select.setLong(1, userId);
            select.setString(2, key);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, null, user, "db", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    // Interaction.
    public List<Interaction> getInteractions() {
        Connection connection = null;
        var interactions = new ArrayList<Interaction>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM interaction_count WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do interactions.add(new Interaction(
                        resultSet.getString("interaction"),
                        resultSet.getInt("shared"),
                        resultSet.getInt("received")
                ));
                while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, null, user, "interaction", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return interactions;
    }

    public int getInteractionCount(String interaction, boolean isShared) {
        Connection connection = null;
        var commandCount = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT " + (isShared ? "shared" : "received") + " FROM interaction_count WHERE user_id=? AND interaction=?");
            select.setLong(1, userId);
            select.setString(2, interaction.toLowerCase());
            var resultSet = select.executeQuery();
            if (resultSet.first()) commandCount = resultSet.getInt(isShared ? "shared" : "received");
        } catch (SQLException e) {
            new Log(e, null, user, "interaction", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return commandCount;
    }

    public void incrementInteractionCount(String interaction, boolean isShared) {
        Connection connection = null;
        var count = getInteractionCount(interaction, isShared);

        try {
            connection = Servant.db.getHikari().getConnection();
            if (hasEntry("interaction_count", "interaction", interaction, false)) {
                var update = connection.prepareStatement("UPDATE interaction_count SET " + (isShared ? "shared" : "received") + "=? WHERE user_id=? AND interaction=?");
                update.setInt(1, count + 1);
                update.setLong(2, userId);
                update.setString(3, interaction.toLowerCase());
                update.executeUpdate();
            } else {
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
        } catch (SQLException e) {
            new Log(e, null, user, "interaction", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Feature counter
    public Map<String, Integer> getFeatureCounts() {
        Connection connection = null;
        var featureCounts = new HashMap<String, Integer>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM feature_count WHERE id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    var feature = resultSet.getString("feature");
                    if (!feature.equals("addgif") && !feature.equals("blacklist")
                            && !feature.equals("eval") && !feature.equals("refresh")
                            && !feature.equals("serverlist") && !feature.equals("shutdown")
                            && !feature.equals("thread"))
                        featureCounts.put(feature, resultSet.getInt("count"));
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            new Log(e, null, user, "featurecount", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        // Sort By Value
        return featureCounts.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private int getFeatureCount(String feature) {
        Connection connection = null;
        var featureCount = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT count FROM feature_count WHERE id=? AND feature=?");
            select.setLong(1, userId);
            select.setString(2, feature.toLowerCase());
            var resultSet = select.executeQuery();
            if (resultSet.first()) featureCount = resultSet.getInt("count");
        } catch (SQLException e) {
            new Log(e, null, user, "featurecount", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return featureCount;
    }

    public void incrementFeatureCount(String feature) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (hasEntry("feature_count", "feature", feature, true)) {
                var count = getFeatureCount(feature);
                var update = connection.prepareStatement("UPDATE feature_count SET count=? WHERE id=? AND feature=?");
                update.setInt(1, count + 1);
                update.setLong(2, userId);
                update.setString(3, feature.toLowerCase());
                update.executeUpdate();
            } else {
                var insert = connection.prepareStatement("INSERT INTO feature_count (id,feature,count) VALUES (?,?,?)");
                insert.setLong(1, userId);
                insert.setString(2, feature.toLowerCase());
                insert.setInt(3, 1);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, null, user, "featurecount", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Level
    public Map<Long, Integer> getExpOnGuilds() {
        Connection connection = null;
        var expOnGuilds = new HashMap<Long, Integer>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM user_exp WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do expOnGuilds.put(resultSet.getLong("guild_id"), resultSet.getInt("exp"));
                while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, null, user, "level", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return expOnGuilds;
    }

    public int getExp(long guildId) {
        Connection connection = null;
        var exp = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM user_exp WHERE user_id=? AND guild_id=?");
            select.setLong(1, userId);
            select.setLong(2, guildId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) exp = resultSet.getInt("exp");
        } catch (SQLException e) {
            new Log(e, null, user, "level", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return exp;
    }

    public void setExp(long guildId, int exp) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (getExp(guildId) == 0) {
                var insert = connection.prepareStatement("INSERT INTO user_exp(user_id,guild_id,exp) VALUES(?,?,?)");
                insert.setLong(1, userId);
                insert.setLong(2, guildId);
                insert.setInt(3, exp);
                insert.executeUpdate();
            } else {
                var currentExp = getExp(guildId);
                exp += currentExp;
                var update = connection.prepareStatement("UPDATE user_exp SET exp=? WHERE user_id=? AND guild_id=?");
                update.setInt(1, exp);
                update.setLong(2, userId);
                update.setLong(3, guildId);
                update.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, null, user, "level", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Feature Count
    public int getTotalFeatureCount() {
        Connection connection = null;
        var feature = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM feature_count WHERE id=? ORDER BY count DESC");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
            do feature += resultSet.getInt("count"); while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, null, user, "featurecount", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return feature;
    }

    public String getFavoriteAnimal(String lang) {
        Connection connection = null;
        var animals = new HashMap<String, Integer>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM feature_count WHERE id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    var feature = resultSet.getString("feature");
                    switch (feature) {
                        case "bird":
                        case "cat":
                        case "dog":
                        case "fox":
                        case "koala":
                        case "panda":
                        case "redpanda":
                        case "sloth":
                            animals.put(feature, resultSet.getInt("count"));
                            break;
                    }
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            new Log(e, null, user, "featurecount", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        if (!animals.isEmpty()) {
            var hasFavoriteAnimal = true;

            var sortedAnimals = animals.entrySet()
                    .stream()
                    .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            if (animals.size() > 1) {
                Map.Entry<String, Integer> firstAnimal = null;
                Map.Entry<String, Integer> secondAnimal = null;

                var i = 0;
                for (var animal : sortedAnimals.entrySet()) {
                    if (i == 0) firstAnimal = animal;
                    else {
                        secondAnimal = animal;
                        break;
                    }
                    i++;
                }

                if (firstAnimal != null && secondAnimal != null && firstAnimal.getValue().equals(secondAnimal.getValue()))
                    hasFavoriteAnimal = false;
            }

            if (hasFavoriteAnimal) return sortedAnimals.entrySet().iterator().next().getKey();
        }

        return LanguageHandler.get(lang, "profile_nofavourite");
    }

    // Patreon, Ranks and stuff
    public boolean isServerBooster() {
        var servantsKingdom = user.getJDA().getGuildById(436925371577925642L);
        var mistlockSanctuary = user.getJDA().getGuildById(237575963439923200L);
        var isServerBooster = false;

        // Servant's Kingdom Booster
        if (servantsKingdom != null) {
            var members = servantsKingdom.getMembers();
            for (var member : members)
                if (member.getUser().equals(user))
                    isServerBooster = member.getRoles().contains(servantsKingdom.getRoleById(639128857747652648L));
        }

        // Mistlock Sanctuary Booster
        if (mistlockSanctuary != null) {
            var members = mistlockSanctuary.getMembers();
            for (var member : members)
                if (member.getUser().equals(user))
                    isServerBooster = member.getRoles().contains(mistlockSanctuary.getRoleById(585536192691568681L));
        }

        return isServerBooster;
    }

    public boolean isDonator() {
        var servantsKingdom = user.getJDA().getGuildById(436925371577925642L);
        var isDonator = false;

        if (servantsKingdom != null) {
            var members = servantsKingdom.getMembers();
            for (var member : members)
                if (member.getUser().equals(user))
                    isDonator = member.getRoles().contains(servantsKingdom.getRoleById(489738762838867969L));
        }

        return isDonator;
    }

    // Example: $10 Patron is also a $1 Patron!
    public boolean is$1Patron() {
        var servantsKingdom = user.getJDA().getGuildById(436925371577925642L);
        var is$1Patron = false;

        if (servantsKingdom != null) {
            if (is$3Patron()) is$1Patron = true;
            else {
                var members = servantsKingdom.getMembers();
                for (var member : members)
                    if (member.getUser().equals(user))
                        is$1Patron = member.getRoles().contains(servantsKingdom.getRoleById(502472440455233547L));
            }
        }

        return is$1Patron;
    }

    public boolean is$3Patron() {
        var servantsKingdom = user.getJDA().getGuildById(436925371577925642L);
        var is$3Patron = false;

        if (servantsKingdom != null) {
            if (is$5Patron()) is$3Patron = true;
            else {
                var members = servantsKingdom.getMembers();
                for (var member : members)
                    if (member.getUser().equals(user))
                        is$3Patron = member.getRoles().contains(servantsKingdom.getRoleById(502472546600353796L));
            }
        }

        return is$3Patron;
    }

    public boolean is$5Patron() {
        var servantsKingdom = user.getJDA().getGuildById(436925371577925642L);
        var is$5Patron = false;

        if (servantsKingdom != null) {
            if (is$10Patron()) is$5Patron = true;
            else {
                var members = servantsKingdom.getMembers();
                for (var member : members)
                    if (member.getUser().equals(user))
                        is$5Patron = member.getRoles().contains(servantsKingdom.getRoleById(502472823638458380L));
            }
        }

        return is$5Patron;
    }

    public boolean is$10Patron() {
        var servantsKingdom = user.getJDA().getGuildById(436925371577925642L);
        var is$10Patron = false;

        if (servantsKingdom != null) {
            if (user.equals(servantsKingdom.getJDA().getSelfUser())) is$10Patron = true; // Servant should always be Saber
            else if (isVIP()) is$10Patron = true;
            else {
                var members = servantsKingdom.getMembers();
                for (var member : members)
                    if (member.getUser().equals(user))
                        is$10Patron = member.getRoles().contains(servantsKingdom.getRoleById(502472869234868224L));
            }
        }

        return is$10Patron;
    }

    public boolean isVIP() {
        var servantsKingdom = user.getJDA().getGuildById(436925371577925642L);
        var isVIP = false;

        if (servantsKingdom != null) {
            if (isCreator()) isVIP = true;
            else {
                var members = servantsKingdom.getMembers();
                for (var member : members)
                    if (member.getUser().equals(user))
                        isVIP = member.getRoles().contains(servantsKingdom.getRoleById(510204269568458753L));
            }
        }

        return isVIP;
    }

    public boolean isCreator() {
        return String.valueOf(userId).equals(Servant.config.getBotOwnerId());
    }
}
