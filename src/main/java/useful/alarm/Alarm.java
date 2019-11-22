// Author: Tancred423 (https://github.com/Tancred423)
package useful.alarm;

import files.language.LanguageHandler;
import moderation.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import java.sql.Timestamp;
import java.util.Date;

public class Alarm {
    public static void check(JDA jda) {
        var users = jda.getUsers();
        for (var user : users) {
            var internalUser = new User(user.getIdLong());
            var alarms = internalUser.getAlarms(user);
            for (var alarm : alarms) {
                var now = Timestamp.from(new Date().toInstant());
                if (now.after(alarm)) {
                    user.openPrivateChannel().queue(privateChannel -> {
                        var title = internalUser.getAlarmTitle(user);
                        var eb = new EmbedBuilder();
                        eb.setColor(internalUser.getColor(null, user));
                        var lang = internalUser.getLanguage(null, user);
                        eb.setAuthor("Alarm", null, user.getEffectiveAvatarUrl());
                        eb.setDescription(title.isEmpty() ? LanguageHandler.get(lang, "alarm_remind") : title);
                        privateChannel.sendMessage(eb.build()).queue(success -> internalUser.unsetAlarm(alarm, user));
                    });
                }
            }
        }
    }
}
