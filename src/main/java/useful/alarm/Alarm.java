// Author: Tancred423 (https://github.com/Tancred423)
package useful.alarm;

import files.language.LanguageHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import java.sql.Timestamp;
import java.util.Date;

public class Alarm {
    public static void check(JDA jda) {
        var users = jda.getUsers();
        for (var user : users) {
            var master = new Master(user);
            var alarms = master.getAlarms();
            for (var alarm : alarms.entrySet()) {
                var now = Timestamp.from(new Date().toInstant());
                if (now.after(alarm.getKey())) {
                    user.openPrivateChannel().queue(privateChannel -> {
                        var lang = master.getLanguage();
                        privateChannel.sendMessage(new EmbedBuilder()
                                .setColor(master.getColor())
                                .setAuthor("Alarm", null, user.getEffectiveAvatarUrl())
                                .setDescription(alarm.getValue().isEmpty() ? LanguageHandler.get(lang, "alarm_remind") : alarm.getValue()).build()
                        ).queue(success -> master.unsetAlarm(alarm.getKey()));
                    });
                }
            }
        }
    }
}
