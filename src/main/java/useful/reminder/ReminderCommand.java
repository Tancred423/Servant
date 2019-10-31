// Author: Tancred423 (https://github.com/Tancred423)
package useful.reminder;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import owner.blacklist.Blacklist;
import utilities.Constants;
import utilities.Parser;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.concurrent.CompletableFuture;

public class ReminderCommand extends Command {
    public ReminderCommand() {
        this.name = "reminder";
        this.aliases = new String[0];
        this.help = "Set up a reminder.";
        this.category = new Category("Useful");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, name)) return;
            if (Blacklist.isBlacklisted(event.getAuthor(), event.getGuild())) return;

            var lang = LanguageHandler.getLanguage(event);
            var p = GuildHandler.getPrefix(event);

            if (event.getArgs().isEmpty()) {
                var description = String.format(LanguageHandler.get(lang, "reminder_description"), p);
                var usage = String.format(LanguageHandler.get(lang, "reminder_usage"), p, name, p, name);
                var hint = LanguageHandler.get(lang, "reminder_hint");
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                return;
            }

            if (event.getGuild() != null) event.getMessage().delete().queue();

            var argsString = event.getArgs();
            var args = argsString.split(" ");

            if (args.length < 2) {
                event.reply(LanguageHandler.get(lang, "reminder_missingargs"));
                return;
            }

            var guild = event.getGuild();
            var author = event.getAuthor();

            try {
                var date = args[0];
                var time = args[1];
                var offset = new User(event.getAuthor().getIdLong()).getOffset(guild, author);
                OffsetDateTime reminderDate;
                try {
                    reminderDate = OffsetDateTime.parse(date + "T" + time + offset);
                } catch (DateTimeParseException e) {
                    event.replyError(LanguageHandler.get(lang, "reminder_invalidinput"));
                    return;
                }

                if (!reminderDate.isAfter(OffsetDateTime.now(ZoneId.of(offset)))) {
                    event.replyError(LanguageHandler.get(lang, "reminder_past"));
                    return;
                }

                var topic = new StringBuilder();
                for (int i = 2; i < args.length; i++) topic.append(args[i]).append(" ");
                if (topic.length() > 1000 || Parser.isSqlInjection(topic.toString())) {
                    event.replyError(LanguageHandler.get(lang, "reminder_invalidtopic"));
                    return;
                }

                var timestamp = Timestamp.from(reminderDate.toInstant());
                var wasSet = Reminder.setReminder(event.getAuthor().getIdLong(), timestamp, topic.toString(), guild, author);
                if (wasSet) event.replySuccess(LanguageHandler.get(lang, "reminder_success"));
                else {
                    event.replyError(LanguageHandler.get(lang, "reminder_fail"));
                }
            } catch (Exception e) {
                event.reply(LanguageHandler.get(lang, "reminder_invalidinput"));
                e.printStackTrace();
            }

            // Statistics.
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
            if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase(), guild, author);
        });
    }
}
