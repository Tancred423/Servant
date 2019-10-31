// Author: Tancred423 (https://github.com/Tancred423)
package useful.reminder;

import files.language.LanguageHandler;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import servant.Log;
import servant.Servant;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static utilities.DatabaseConn.closeQuietly;

public class Reminder {
    static boolean setReminder(long userId, Timestamp reminderTime, String topic, Guild guild, net.dv8tion.jda.core.entities.User user) {
        Connection connection = null;
        var wasSet = false;

        try {
            if (!reminderHasEntry(userId, reminderTime, guild, user)) {
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

    private static void unsetReminder(long userId, Timestamp reminderTime, net.dv8tion.jda.core.entities.User user) {
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

    private static boolean reminderHasEntry(long userId, Timestamp reminderTime, Guild guild, net.dv8tion.jda.core.entities.User user) {
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

    private static Map<Timestamp, String> getReminders(long userId, net.dv8tion.jda.core.entities.User user) {
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

    public static void check(JDA jda) {
        var users = jda.getUsers();
        for (var user : users) {
            var reminders = getReminders(user.getIdLong(), user);
            for (var reminder : reminders.entrySet()) {
                var now = Timestamp.from(new Date().toInstant());
                if (now.after(reminder.getKey())) {
                    user.openPrivateChannel().queue(privateChannel -> {
                        var eb = new EmbedBuilder();
                        eb.setColor(new User(user.getIdLong()).getColor(null, user));
                        eb.setAuthor("Reminder", null, user.getEffectiveAvatarUrl());
                        var lang = new User(user.getIdLong()).getLanguage(null, user);

                        eb.setDescription(reminder.getValue().isEmpty() ?
                                LanguageHandler.get(lang, "reminder_remind_notopic") :
                                String.format(LanguageHandler.get(lang, "reminder_remind_topic"), reminder.getValue()));
                        privateChannel.sendMessage(eb.build()).queue(success -> unsetReminder(user.getIdLong(), reminder.getKey(), user));
                    });
                }
            }
        }
    }
}
