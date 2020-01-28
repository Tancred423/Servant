// Author: Tancred423 (https://github.com/Tancred423)
package useful.reminder;

import files.language.LanguageHandler;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import java.sql.Timestamp;
import java.util.Date;

public class Reminder {
    public static void check(JDA jda) {
        var users = jda.getUsers();
        for (var user : users) {
            var master = new Master(user);
            var reminders = master.getReminders();
            for (var reminder : reminders.entrySet()) {
                var now = Timestamp.from(new Date().toInstant());
                if (now.after(reminder.getKey())) {
                    user.openPrivateChannel().queue(privateChannel -> {
                        var eb = new EmbedBuilder();
                        eb.setColor(new Master(user).getColor());
                        eb.setAuthor("Reminder", null, user.getEffectiveAvatarUrl());
                        var lang = new Master(user).getLanguage();

                        privateChannel.sendMessage(eb.setDescription(reminder.getValue().isEmpty() ?
                                LanguageHandler.get(lang, "reminder_remind_notopic") :
                                String.format(LanguageHandler.get(lang, "reminder_remind_topic"), reminder.getValue())
                        ).build()).queue(success -> master.unsetReminder(reminder.getKey()));
                    });
                }
            }
        }
    }
}
