// Author: Tancred423 (https://github.com/Tancred423)
package useful.reminder;

import files.language.LanguageHandler;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;

import java.sql.Timestamp;
import java.util.Date;

public class Reminder {
    public static void check(JDA jda) {
        var users = jda.getUsers();
        for (var user : users) {
            var internalUser = new User(user.getIdLong());
            var reminders = internalUser.getReminders(user);
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
                        privateChannel.sendMessage(eb.build()).queue(success -> internalUser.unsetReminder(reminder.getKey(), user));
                    });
                }
            }
        }
    }
}
