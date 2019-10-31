// Author: Tancred423 (https://github.com/Tancred423)
package useful.alarm;

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
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

import static utilities.DatabaseConn.closeQuietly;

public class Alarm {
    static ZonedDateTime getDate(ZonedDateTime date, String timeString) throws NumberFormatException {
        var timeArray = timeString.split(" ");
        String days;
        String hours;
        String minutes;

        for (var time : timeArray)
            if (time.toLowerCase().endsWith("d")) {
                days = time.replaceAll("d", "");
                date = date.plusDays(Integer.parseInt(days));
            } else if (time.toLowerCase().endsWith("h")) {
                hours = time.replaceAll("h", "");
                date = date.plusHours(Integer.parseInt(hours));
            } else if (time.toLowerCase().endsWith("m")) {
                minutes = time.replaceAll("m", "");
                date = date.plusMinutes(Integer.parseInt(minutes));
            }

        date = date.truncatedTo(ChronoUnit.MINUTES);
        return date;
    }

    static boolean setAlarm(long userId, Timestamp alarmTime, Guild guild, net.dv8tion.jda.core.entities.User user) {
        var wasSet = false;

        if (!alarmHasEntry(userId, alarmTime, guild, user)) {
            Connection connection = null;

            try {
                connection = Servant.db.getHikari().getConnection();
                var insert = connection.prepareStatement("INSERT INTO alarm (user_id,alarm_time) VALUES (?,?)");
                insert.setLong(1, userId);
                insert.setTimestamp(2, alarmTime);
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

    private static void unsetAlarm(long userId, Timestamp alarmTime, net.dv8tion.jda.core.entities.User user) {
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

    private static boolean alarmHasEntry(long userId, Timestamp alarmTime, Guild guild, net.dv8tion.jda.core.entities.User user) {
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

    private static List<Timestamp> getAlarms(long userId, net.dv8tion.jda.core.entities.User user) {
        Connection connection = null;
        var reminders = new ArrayList<Timestamp>();

        try {
            connection = Servant.db.getHikari().getConnection();
            var select = connection.prepareStatement("SELECT * FROM alarm WHERE user_id=?");
            select.setLong(1, userId);
            var resultSet = select.executeQuery();
            if (resultSet.first())
                do reminders.add(resultSet.getTimestamp("alarm_time"));
                while (resultSet.next());
        } catch (SQLException e) {
            new Log(e, null, user, "alarm", null).sendLog(false);
        } finally {
            closeQuietly(connection);
        }

        return reminders;
    }

    public static void check(JDA jda) {
        var users = jda.getUsers();
        for (var user : users) {
            var alarms = getAlarms(user.getIdLong(), user);
            for (var alarm : alarms) {
                var now = Timestamp.from(new Date().toInstant());
                if (now.after(alarm)) {
                    user.openPrivateChannel().queue(privateChannel -> {
                        var eb = new EmbedBuilder();
                        eb.setColor(new User(user.getIdLong()).getColor(null, user));
                        var lang = new User(user.getIdLong()).getLanguage(null, user);
                        eb.setAuthor("Alarm", null, user.getEffectiveAvatarUrl());
                        eb.setDescription(LanguageHandler.get(lang, "alarm_remind"));
                        privateChannel.sendMessage(eb.build()).queue(success -> unsetAlarm(user.getIdLong(), alarm, user));
                    });
                }
            }
        }
    }
}
