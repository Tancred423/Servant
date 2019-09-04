// Author: Tancred423 (https://github.com/Tancred423)
package useful.reminder;

import files.language.LanguageHandler;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import servant.Database;
import servant.Log;
import servant.Servant;

import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Reminder {
    static boolean setReminder(long userId, Timestamp reminderTime, String topic) throws SQLException {
        if (reminderHasEntry(userId, reminderTime)) return false;
        var connection = Database.getConnection();
        var insert = connection.prepareStatement("INSERT INTO reminder (user_id,reminder_time,topic) VALUES (?,?,?)");
        insert.setLong(1, userId);
        insert.setTimestamp(2, reminderTime);
        insert.setString(3, topic);
        insert.executeUpdate();
        connection.close();
        return true;
    }

    private static void unsetReminder(long userId, Timestamp reminderTime) throws SQLException {
        var connection = Database.getConnection();
        var insert = connection.prepareStatement("DELETE FROM reminder WHERE user_id=? AND reminder_time=?");
        insert.setLong(1, userId);
        insert.setTimestamp(2, reminderTime);
        insert.executeUpdate();
        connection.close();
    }

    private static boolean reminderHasEntry(long userId, Timestamp reminderTime) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM reminder WHERE user_id=? AND reminder_time=?");
        select.setLong(1, userId);
        select.setTimestamp(2, reminderTime);
        var resultSet = select.executeQuery();
        var hasEntry = false;
        if (resultSet.first()) hasEntry = true;
        connection.close();
        return hasEntry;
    }

    private static Map<Timestamp, String> getReminders(long userId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM reminder WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        var reminders = new HashMap<Timestamp, String>();
        if (resultSet.first())
            do reminders.put(resultSet.getTimestamp("reminder_time"), resultSet.getString("topic"));
            while (resultSet.next());
        connection.close();
        return reminders;
    }

    public static void check(JDA jda) throws SQLException {
        var users = jda.getUsers();
        for (var user : users) {
            var reminders = getReminders(user.getIdLong());
            for (var reminder : reminders.entrySet()) {
                var now = Timestamp.from(new Date().toInstant());
                if (now.after(reminder.getKey())) {
                    user.openPrivateChannel().queue(privateChannel -> {
                        var eb = new EmbedBuilder();
                        try {
                            eb.setColor(new User(user.getIdLong()).getColor());
                        } catch (SQLException e) {
                            eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
                        }
                        eb.setAuthor("Reminder", null, user.getEffectiveAvatarUrl());

                        String lang;
                        try {
                            lang = new User(user.getIdLong()).getLanguage();
                        } catch (SQLException e) {
                            lang = Servant.config.getDefaultLanguage();
                        }

                        eb.setDescription(reminder.getValue().isEmpty() ?
                                LanguageHandler.get(lang, "reminder_remind_notopic") :
                                String.format(LanguageHandler.get(lang, "reminder_remind_topic"), reminder.getValue()));
                        privateChannel.sendMessage(eb.build()).queue(success -> {
                            try {
                                unsetReminder(user.getIdLong(), reminder.getKey());
                            } catch (SQLException e) {
                                new Log(e, null, user, "reminder", null).sendLog(false);
                            }
                        });
                    });
                }
            }
        }
    }
}
