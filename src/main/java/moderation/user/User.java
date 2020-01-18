// Author: Tancred423 (https://github.com/Tancred423)
package moderation.user;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.entities.Guild;
import patreon.PatreonHandler;
import servant.Log;
import servant.Servant;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

import static utilities.DatabaseConn.closeQuietly;

public class User {
    private long userId;

    public User(long userId) {
        this.userId = userId;
    }

    // Alarm
    public boolean setAlarm(Timestamp alarmTime, String title, Guild guild, net.dv8tion.jda.api.entities.User user) {
        var wasSet = false;

        if (!alarmHasEntry(alarmTime, guild, user)) {
            Connection connection = null;

            try {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO alarm (user_id,alarm_time,title) VALUES (?,?,?)");
                insert.setLong(1, userId);
                insert.setTimestamp(2, alarmTime);
                insert.setString(3, title);
                insert.executeUpdate();
                wasSet = true;
            } catch (SQLException e) {
                new Log(e, guild, user, "alarm", null).sendLog(false);
            } finally {
                closeQuietly(connection);
            }
        }

        return wasSet;
    }

    public void unsetAlarm(Timestamp alarmTime, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var insert = connection.prepareStatement("DELETE FROM alarm WHERE user_id=? AND alarm_time=?");
            insert.setLong(1, userId);
            insert.setTimestamp(2, alarmTime);
            insert.executeUpdate();
        } catch (SQLException e) {
            new Log(e, null, user, "alarm", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    private boolean alarmHasEntry(Timestamp alarmTime, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM alarm WHERE user_id=? AND alarm_time=?");
            select.setLong(1, userId);
            select.setTimestamp(2, alarmTime);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "alarm", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public String getAlarmTitle(net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        String title = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT title FROM alarm WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) title = resultSet.getString("title");
        } catch (SQLException e) {
            new Log(e, null, user, "alarm", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return title;
    }

    public List<Timestamp> getAlarms(net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var alarms = new ArrayList<Timestamp>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM alarm WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do alarms.add(resultSet.getTimestamp("alarm_time"));
                while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, null, user, "alarm", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return alarms;
    }

    // Reminder
    public boolean setReminder(Timestamp reminderTime, String topic, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var wasSet = false;

        try {
            if (!reminderHasEntry(reminderTime, guild, user)) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO reminder (user_id,reminder_time,topic) VALUES (?,?,?)");
                insert.setLong(1, userId);
                insert.setTimestamp(2, reminderTime);
                insert.setString(3, topic);
                insert.executeUpdate();
                wasSet = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasSet;
    }

    public void unsetReminder(Timestamp reminderTime, net.dv8tion.jda.api.entities.User user) {
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

    private boolean reminderHasEntry(Timestamp reminderTime, Guild guild, net.dv8tion.jda.api.entities.User user) {
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
            new Log(e, guild, user, "levelrole", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public Map<Timestamp, String> getReminders(net.dv8tion.jda.api.entities.User user) {
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
    public String getBio(Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var text = "";

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT text FROM bio WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) text = resultSet.getString("text");
        } catch (SQLException e) {
            new Log(e, guild, user, "bio", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return text;
    }

    private boolean bioHasEntry(Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT text FROM bio WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "bio", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void setBio(String text, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (bioHasEntry(guild, user)) {
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
            new Log(e, guild, user, "bio", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Baguette
    public Map<Integer, Integer> getBaguette(Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var baguette = new HashMap<Integer, Integer>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM baguette_counter WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) baguette.put(resultSet.getInt("baguette_size"), resultSet.getInt("size_counter"));
        } catch (SQLException e) {
            new Log(e, guild, user, "baguette", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return baguette;
    }

    private boolean baguetteHasEntry(Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM baguette_counter WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "baguette", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    public void setBaguette(int baguetteSize, int sizeCounter, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (baguetteHasEntry(guild, user)) {
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
            new Log(e, guild, user, "baguette", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Achievement
    public boolean hasAchievement(String achievement, Guild guild, net.dv8tion.jda.api.entities.User user) {
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
            new Log(e, guild, user, "achievement", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasAchievement;
    }

    public Map<String, Integer> getAchievements(Guild guild, net.dv8tion.jda.api.entities.User user) {
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
            new Log(e, guild, user, "achievement", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return achievements;
    }

    public List<String> getLevelAchievements(Guild guild, net.dv8tion.jda.api.entities.User user) {
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
            new Log(e, guild, user, "achievement", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return achievements;
    }

    public int getTotalAP(Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var ap = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM achievement WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) do ap += resultSet.getInt("ap"); while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, guild, user, "achievement", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return ap;
    }

    public void setAchievement(String achievement, int ap, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;

        try {
            if (!hasAchievement(achievement, guild, user)) {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO achievement (user_id,achievement,ap) VALUES (?,?,?)");
                insert.setLong(1, userId);
                insert.setString(2, achievement.toLowerCase());
                insert.setInt(3, ap);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "achievement", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    public void unsetAchievement(String achievement, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;

        try {
            if (hasAchievement(achievement, guild, user)) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM achievement WHERE user_id=? AND achievement=?");
                delete.setLong(1, userId);
                delete.setString(2, achievement.toLowerCase());
                delete.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "achievement", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Offset
    public String getOffset(Guild guild, net.dv8tion.jda.api.entities.User user) {
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
            new Log(e, guild, user, "offset", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return offset;
    }

    private boolean offsetHasEntry(Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT offset FROM user WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "offset", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    void setOffset(String offset, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (offsetHasEntry(guild, user)) {
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
            new Log(e, guild, user, "offset", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    void unsetOffset(Guild guild, net.dv8tion.jda.api.entities.User user) {
        setOffset(Servant.config.getDefaultOffset(), guild, user);
    }

    // Prefix
    public String getPrefix(Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        String prefix = Servant.config.getDefaultPrefix();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT prefix FROM user WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) prefix = resultSet.getString("prefix");
        } catch (SQLException e) {
            new Log(e, guild, user, "prefix", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return prefix;
    }

    private boolean prefixHasEntry(Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT prefix FROM user WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "prefix", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    void setPrefix(String prefix, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (prefixHasEntry(guild, user)) {
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
            new Log(e, guild, user, "prefix", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    void unsetPrefix(Guild guild, net.dv8tion.jda.api.entities.User user) {
        setPrefix(Servant.config.getDefaultPrefix(), guild, user);
    }

    // Language
    public String getLanguage(Guild guild, net.dv8tion.jda.api.entities.User user) {
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
            new Log(e, guild, user, "language", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return language;
    }

    private boolean languageHasEntry(Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var hasEntry = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT language FROM user WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            hasEntry = resultSet.first();
        } catch (SQLException e) {
            new Log(e, guild, user, "language", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    void setLanguage(String language, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (languageHasEntry(guild, user)) {
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
            new Log(e, guild, user, "language", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    void unsetLanguage(Guild guild, net.dv8tion.jda.api.entities.User user) {
        setLanguage(Servant.config.getDefaultLanguage(), guild, user);
    }

    // Stream Hidden
    public boolean isStreamHidden(long guildId, Guild guild, net.dv8tion.jda.api.entities.User user) {
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
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isStreamHidden;
    }

    List<Long> getStreamHiddenGuilds(Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var streamHiddenGuilds = new ArrayList<Long>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM streamhidden WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) do streamHiddenGuilds.add(resultSet.getLong("guild_id")); while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return streamHiddenGuilds;
    }

    boolean toggleStreamHidden(long guildId, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var isStreamHidden = false;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (!isStreamHidden(guildId, guild, user)) {
                var insert = connection.prepareStatement("INSERT INTO streamhidden (guild_id,user_id) VALUES (?,?)");
                insert.setLong(1, guildId);
                insert.setLong(2, userId);
                insert.executeUpdate();
                isStreamHidden = true;
            } else {
                unsetStreamHidden(guildId, guild, user);
                isStreamHidden = false;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return isStreamHidden;
    }

    private void unsetStreamHidden(long guildId, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            var delete = connection.prepareStatement("DELETE FROM streamhidden WHERE guild_id=? AND user_id=?");
            delete.setLong(1, guildId);
            delete.setLong(2, userId);
            delete.executeUpdate();
        } catch (SQLException e) {
            new Log(e, guild, user, "livestream", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Color
    public String getColorCode(Guild guild, net.dv8tion.jda.api.entities.User user) {
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
                if (servantsKingdom == null) return colorCode; // todo: always null?
                Color color = null;
                if (PatreonHandler.isVIP(user)) {
                    var role = servantsKingdom.getRoleById(510204269568458753L);
                    if (role != null) color = role.getColor();
                }
                else if (PatreonHandler.is$10Patron(user)) {
                    var role = servantsKingdom.getRoleById(502472869234868224L);
                    if (role != null) color = role.getColor();
                }
                else if (PatreonHandler.is$5Patron(user)) {
                    var role = servantsKingdom.getRoleById(502472823638458380L);
                    if (role != null) color = role.getColor();
                }
                else if (PatreonHandler.is$3Patron(user)) {
                    var role = servantsKingdom.getRoleById(502472546600353796L);
                    if (role != null) color = role.getColor();
                }
                else if (PatreonHandler.is$1Patron(user)) {
                    var role = servantsKingdom.getRoleById(502472440455233547L);
                    if (role != null) color = role.getColor();
                }
                else if (PatreonHandler.isDonator(user)) {
                    var role = servantsKingdom.getRoleById(489738762838867969L);
                    if (role != null) color = role.getColor();
                }
                else if (PatreonHandler.isServerBooster(user)) {
                    var role = servantsKingdom.getRoleById(639128857747652648L);
                    if (role != null) color = role.getColor();
                }

                if (color != null) colorCode = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "color", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return colorCode;
    }

    public Color getColor(Guild guild, net.dv8tion.jda.api.entities.User author) {
        Connection connection = null;
        var color = Color.decode(Servant.config.getDefaultColorCode());
        var user = author.getJDA().getUserById(userId);
        if (user == null) return color;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT value FROM user_settings WHERE user_id=? AND setting=?");
            select.setLong(1, userId);
            select.setString(2, "color");
            var resultSet = select.executeQuery();
            if (resultSet.first()) color = Color.decode(resultSet.getString("value"));
            else {
                var servantsKingdom = user.getJDA().getGuildById(436925371577925642L);
                if (servantsKingdom == null) return color; // todo: always null?
                if (PatreonHandler.isVIP(user)) {
                    var role = servantsKingdom.getRoleById(510204269568458753L);
                    if (role != null) color = role.getColor();
                }
                else if (PatreonHandler.is$10Patron(user)) {
                    var role = servantsKingdom.getRoleById(502472869234868224L);
                    if (role != null) color = role.getColor();
                }
                else if (PatreonHandler.is$5Patron(user)) {
                    var role = servantsKingdom.getRoleById(502472823638458380L);
                    if (role != null) color = role.getColor();
                }
                else if (PatreonHandler.is$3Patron(user)) {
                    var role = servantsKingdom.getRoleById(502472546600353796L);
                    if (role != null) color = role.getColor();
                }
                else if (PatreonHandler.is$1Patron(user)) {
                    var role = servantsKingdom.getRoleById(502472440455233547L);
                    if (role != null) color = role.getColor();
                }
                else if (PatreonHandler.isDonator(user)) {
                    var role = servantsKingdom.getRoleById(489738762838867969L);
                    if (role != null) color = role.getColor();
                }
                else if (PatreonHandler.isServerBooster(user)) {
                    var role = servantsKingdom.getRoleById(639128857747652648L);
                    if (role != null) color = role.getColor();
                }
            }
        } catch (SQLException e) {
            new Log(e, guild, author, "color", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return color;
    }

    public void setColor(String colorCode, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (hasEntry("user_settings", "setting", "color", false, guild, user)) {
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
            new Log(e, guild, user, "color", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    boolean unsetColor(Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var wasUnset = false;

        try {
            if (hasEntry("user_settings", "setting", "color", false, guild, user)) {
                connection = Servant.db.getHikari().getConnection();
                var delete = connection.prepareStatement("DELETE FROM user_settings WHERE user_id=? AND setting=?");
                delete.setLong(1, userId);
                delete.setString(2, "color");
                delete.executeUpdate();
                wasUnset = true;
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "color", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return wasUnset;
    }

    // DB
    private boolean hasEntry(String tableName, String column, String key, boolean isFeatureCount,
                             Guild guild, net.dv8tion.jda.api.entities.User user) {
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
            new Log(e, guild, user, "db", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return hasEntry;
    }

    // Interaction.
    public int getInteractionCount(String interaction, boolean isShared, Guild guild, net.dv8tion.jda.api.entities.User user) {
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
            new Log(e, guild, user, "interaction", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return commandCount;
    }

    public void incrementInteractionCount(String interaction, boolean isShared, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;
        var count = getInteractionCount(interaction, isShared, guild, user);

        try {
            connection = Servant.db.getHikari().getConnection();
            if (hasEntry("interaction_count", "interaction", interaction, false, guild, user)) {
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
            new Log(e, guild, user, "interaction", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Feature counter
    private int getFeatureCount(String feature, Guild guild, net.dv8tion.jda.api.entities.User user) {
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
            new Log(e, guild, user, "featurecount", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return featureCount;
    }

    public void incrementFeatureCount(String feature, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (hasEntry("feature_count", "feature", feature, true, guild, user)) {
                var count = getFeatureCount(feature, guild, user);
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
            new Log(e, guild, user, "featurecount", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Level
    public int getExp(long guildId, Guild guild, net.dv8tion.jda.api.entities.User user) {
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
            new Log(e, guild, user, "level", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return exp;
    }

    public void setExp(long guildId, int exp, Guild guild, net.dv8tion.jda.api.entities.User user) {
        Connection connection = null;

        try {
            connection = Servant.db.getHikari().getConnection();
            if (getExp(guildId, guild, user) == 0) {
                var insert = connection.prepareStatement("INSERT INTO user_exp(user_id,guild_id,exp) VALUES(?,?,?)");
                insert.setLong(1, userId);
                insert.setLong(2, guildId);
                insert.setInt(3, exp);
                insert.executeUpdate();
            } else {
                var currentExp = getExp(guildId, guild, user);
                exp += currentExp;
                var update = connection.prepareStatement("UPDATE user_exp SET exp=? WHERE user_id=? AND guild_id=?");
                update.setInt(1, exp);
                update.setLong(2, userId);
                update.setLong(3, guildId);
                update.executeUpdate();
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "level", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }
    }

    // Feature Count
    public int getTotalFeatureCount(Guild guild, net.dv8tion.jda.api.entities.User user) {
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
            new Log(e, guild, user, "featurecount", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return feature;
    }

    public Map<String, Integer> getTop10MostUsedFeatures(Guild guild, net.dv8tion.jda.api.entities.User user, String lang) {
        Connection connection = null;
        var features = new LinkedHashMap<String, Integer>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM feature_count WHERE id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    var feature = resultSet.getString("feature");
                    var count = resultSet.getInt("count");

                    features.put(feature, count);
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "featurecount", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        if (!features.isEmpty()) {
            var sortedFeatures = new LinkedHashMap<String, Integer>();
            sortedFeatures.put(LanguageHandler.get(lang, "profile_total_muc"), getTotalFeatureCount(guild, user));
            var counter = 0;

            List<Map.Entry<String, Integer>> entries = new ArrayList<>(features.entrySet());
            entries.sort(Map.Entry.comparingByValue());
            Collections.reverse(entries);
            for (Map.Entry<String, Integer> entry : entries) {
                if (counter >= 9) break;
                sortedFeatures.put(entry.getKey(), entry.getValue());
                counter++;
            }
            return sortedFeatures;
        } else return features;
    }

    public String getFavouriteAnimal(Guild guild, net.dv8tion.jda.api.entities.User user, String lang) {
        Connection connection = null;
        var bird = 0;
        var cat = 0;
        var dog = 0;

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM feature_count WHERE id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first()) {
                do {
                    var feature = resultSet.getString("feature");
                    if (feature.equals("bird")) bird = resultSet.getInt("count");
                    if (feature.equals("cat")) cat = resultSet.getInt("count");
                    if (feature.equals("dog")) dog = resultSet.getInt("count");
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            new Log(e, guild, user, "featurecount", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        if (bird > cat && bird > dog) return LanguageHandler.get(lang, "profile_bird");
        else if (cat > bird && cat > dog) return LanguageHandler.get(lang, "profile_cat");
        else if (dog > bird && dog > cat) return LanguageHandler.get(lang, "profile_dog");
        else return LanguageHandler.get(lang, "profile_nofavourite");
    }
}
