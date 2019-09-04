// Author: Tancred423 (https://github.com/Tancred423)
package useful.alarm;

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
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

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

    static boolean setAlarm(long userId, Timestamp alarmTime) throws SQLException {
        if (alarmHasEntry(userId, alarmTime)) return false;
        var connection = Database.getConnection();
        var insert = connection.prepareStatement("INSERT INTO alarm (user_id,alarm_time) VALUES (?,?)");
        insert.setLong(1, userId);
        insert.setTimestamp(2, alarmTime);
        insert.executeUpdate();
        connection.close();
        return true;
    }

    private static void unsetAlarm(long userId, Timestamp alarmTime) throws SQLException {
        var connection = Database.getConnection();
        var insert = connection.prepareStatement("DELETE FROM alarm WHERE user_id=? AND alarm_time=?");
        insert.setLong(1, userId);
        insert.setTimestamp(2, alarmTime);
        insert.executeUpdate();
        connection.close();
    }

    private static boolean alarmHasEntry(long userId, Timestamp alarmTime) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM alarm WHERE user_id=? AND alarm_time=?");
        select.setLong(1, userId);
        select.setTimestamp(2, alarmTime);
        var resultSet = select.executeQuery();
        var hasEntry = false;
        if (resultSet.first()) hasEntry = true;
        connection.close();
        return hasEntry;
    }

    private static List<Timestamp> getAlarms(long userId) throws SQLException {
        var connection = Database.getConnection();
        var select = connection.prepareStatement("SELECT * FROM alarm WHERE user_id=?");
        select.setLong(1, userId);
        var resultSet = select.executeQuery();
        var reminders = new ArrayList<Timestamp>();
        if (resultSet.first())
            do reminders.add(resultSet.getTimestamp("alarm_time"));
            while (resultSet.next());
        connection.close();
        return reminders;
    }

    public static void check(JDA jda) throws SQLException {
        var users = jda.getUsers();
        for (var user : users) {
            var alarms = getAlarms(user.getIdLong());
            for (var alarm : alarms) {
                var now = Timestamp.from(new Date().toInstant());
                if (now.after(alarm)) {
                    user.openPrivateChannel().queue(privateChannel -> {
                        var eb = new EmbedBuilder();
                        try {
                            eb.setColor(new User(user.getIdLong()).getColor());
                        } catch (SQLException e) {
                            eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
                        }

                        String lang;
                        try {
                            lang = new User(user.getIdLong()).getLanguage();
                        } catch (SQLException e) {
                            lang = Servant.config.getDefaultLanguage();
                        }

                        eb.setAuthor("Alarm", null, user.getEffectiveAvatarUrl());
                        eb.setDescription(LanguageHandler.get(lang, "alarm_remind"));
                        privateChannel.sendMessage(eb.build()).queue(success -> {
                            try {
                                unsetAlarm(user.getIdLong(), alarm);
                            } catch (SQLException e) {
                                new Log(e, null, user, "alarm", null).sendLog(false);
                            }
                        });
                    });
                }
            }
        }
    }
}
