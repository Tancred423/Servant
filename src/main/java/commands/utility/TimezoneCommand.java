// Author: Tancred423 (https://github.com/Tancred423)
package commands.utility;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import servant.LoggingTask;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.MessageUtil;
import utilities.TimezoneUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class TimezoneCommand extends Command {
    public TimezoneCommand() {
        this.name = "timezone";
        this.aliases = new String[0];
        this.help = "Converter";
        this.category = new Category("Utility");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = false;
        this.ownerCommand = false;
        this.cooldown = Constants.USER_COOLDOWN;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[0];
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var jda = event.getJDA();

        var lang = LanguageHandler.getLanguage(event);
        var p = event.getGuild() == null ? Servant.config.getDefaultPrefix() : new MyGuild(event.getGuild()).getPrefix();

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "timezone_description");
            var usage = String.format(LanguageHandler.get(lang, "timezone_usage"), p, name, p, name);
            var hint = LanguageHandler.get(lang, "timezone_hint");
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), lang, description, aliases, usage, hint));
            return;
        }

        var argsString = event.getArgs();
        var args = argsString.split(" ");

        if (args.length < 4) {
            event.reply(LanguageHandler.get(lang, "timezone_missingargs"));
            return;
        }

        var date = args[0];
        var time = args[1];
        if (time.length() == 4) time = "0" + time; // 3:00 to 03:00
        var zone = TimezoneUtil.getOffset(args[2]);
        var targetZone = TimezoneUtil.getOffset(args[3]);

        if (zone == null) {
            event.reply(LanguageHandler.get(lang, "timezone_invalidzone_start"));
            event.reactError();
            return;
        }

        if (targetZone == null) {
            event.reply(LanguageHandler.get(lang, "timezone_invalidzone_target"));
            event.reactError();
            return;
        }

        try {
            var start = OffsetDateTime.parse(date + "T" + time + (zone.equals("00:00") ? "Z" : zone));
            var formatStart = start.format(DateTimeFormatter.RFC_1123_DATE_TIME);
            var offsetStart = start.getOffset().toString().replaceAll(":", "");
            formatStart = formatStart.replaceAll(Pattern.quote(offsetStart), args[2].toUpperCase());

            var target = start.withOffsetSameInstant(ZoneOffset.of(targetZone.equals("00:00") ? "Z" : targetZone));
            var formatTarget = target.format(DateTimeFormatter.RFC_1123_DATE_TIME);
            var offsetTarget = target.getOffset().toString().replaceAll(":", "");
            formatTarget = formatTarget.replaceAll(Pattern.quote(offsetTarget), args[3].toUpperCase());

            var eb = new EmbedBuilder();
            eb.setColor(Color.decode(new MyUser(event.getAuthor()).getColorCode()));
            eb.setTitle(LanguageHandler.get(lang, "timezone_conversion"));
            eb.addField(LanguageHandler.get(lang, "timezone_input"), formatStart, false);
            eb.addField(LanguageHandler.get(lang, "timezone_output"), formatTarget, false);
            event.reply(eb.build());
        } catch (Exception e) {
            Servant.fixedThreadPool.submit(new LoggingTask(e, jda, "TimezoneCommand#execute"));
            event.reply(LanguageHandler.get(lang, "timezone_invalid"));
            event.reactError();
        }
    }
}
