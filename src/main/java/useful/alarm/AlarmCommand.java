// Author: Tancred423 (https://github.com/Tancred423)
package useful.alarm;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import servant.Servant;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class AlarmCommand extends Command {
    public AlarmCommand() {
        this.name = "alarm";
        this.aliases = new String[0];
        this.help = "Set up an alarm.";
        this.category = new Category("Useful");
        this.arguments = "[time]";
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
        if (!Toggle.isEnabled(event, name)) return;

        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        if (event.getArgs().isEmpty()) {
            try {
                var description = String.format(LanguageHandler.get(lang, "alarm_description"), p);
                var usage = String.format(LanguageHandler.get(lang, "alarm_usage"), p, name, p, name);
                var hint = String.format(LanguageHandler.get(lang, "alarm_hint"), p, name);
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        var argsString = event.getArgs();
        var args = argsString.split(" ");
        String days;
        String hours;
        String minutes;

        for (var arg : args) {
            if (arg.toLowerCase().endsWith("d")) {
                days = arg.replaceAll("d", "");
                if (!days.matches("[0-9]+")) {
                    event.replyError(LanguageHandler.get(lang, "alarm_invalidtime"));
                    event.reactError();
                    return;
                }
            } else if (arg.toLowerCase().endsWith("h")) {
                hours = arg.replaceAll("h", "");
                if (!hours.matches("[0-9]+")) {
                    event.replyError(LanguageHandler.get(lang, "alarm_invalidtime"));
                    event.reactError();
                    return;
                }
            } else if (arg.toLowerCase().endsWith("m")) {
                minutes = arg.replaceAll("m", "");
                if (!minutes.matches("[0-9]+")) {
                    event.replyError(LanguageHandler.get(lang, "alarm_invalidtime"));
                    event.reactError();
                    return;
                }
            } else {
                event.replyError(LanguageHandler.get(lang, "alarm_invalidtime"));
                event.reactError();
                return;
            }
        }

        ZonedDateTime now;
        try {
            now = ZonedDateTime.now(ZoneOffset.of(new User(event.getAuthor().getIdLong()).getOffset()));
        } catch (SQLException e) {
            now = ZonedDateTime.now(ZoneOffset.of(Servant.config.getDefaultOffset()));
        }

        ZonedDateTime alarmDate;
        try {
            alarmDate = Alarm.getDate(now, argsString.trim());
        } catch (NumberFormatException e) {
            event.replyError(LanguageHandler.get(lang, "alarm_wrongargument"));
            event.reactError();
            return;
        }

        var timestamp = Timestamp.from(alarmDate.toInstant());
        try {
            if (Alarm.setAlarm(event.getAuthor().getIdLong(), timestamp)) event.reactSuccess();
            else {
                event.replyError(LanguageHandler.get(lang, "alarm_alreadyset"));
                event.reactError();
            }
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
        }

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
