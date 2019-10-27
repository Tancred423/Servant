// Author: Tancred423 (https://github.com/Tancred423)
package useful.timezone;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import owner.blacklist.Blacklist;
import servant.Log;
import servant.Servant;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.awt.*;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class TimezoneCommand extends Command {
    public TimezoneCommand() {
        this.name = "timezone";
        this.aliases = new String[0];
        this.help = "Converter.";
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

            var lang = LanguageHandler.getLanguage(event, name);
            var p = GuildHandler.getPrefix(event, name);

            if (event.getArgs().isEmpty()) {
                try {
                    var description = LanguageHandler.get(lang, "timezone_description");
                    var usage = String.format(LanguageHandler.get(lang, "timezone_usage"), p, name, p, name);
                    var hint = LanguageHandler.get(lang, "timezone_hint");
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }
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
            var zone = Timezone.getOffset(args[2]);
            var targetZone = Timezone.getOffset(args[3]);

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

                var target = start.withOffsetSameInstant(ZoneOffset.of(targetZone));
                var formatTarget = target.format(DateTimeFormatter.RFC_1123_DATE_TIME);
                var offsetTarget = target.getOffset().toString().replaceAll(":", "");
                formatTarget = formatTarget.replaceAll(Pattern.quote(offsetTarget), args[3].toUpperCase());

                var eb = new EmbedBuilder();
                try {
                    eb.setColor(new User(event.getAuthor().getIdLong()).getColor());
                } catch (SQLException e) {
                    eb.setColor(Color.decode(Servant.config.getDefaultColorCode()));
                }
                eb.setTitle(LanguageHandler.get(lang, "timezone_conversion"));
                eb.addField(LanguageHandler.get(lang, "timezone_input"), formatStart, false);
                eb.addField(LanguageHandler.get(lang, "timezone_output"), formatTarget, false);
                event.reply(eb.build());
            } catch (Exception e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
                event.reply(LanguageHandler.get(lang, "timezone_invalid"));
                event.reactError();
            }

            // Statistics.
            try {
                new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
                if (event.getGuild() != null) new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
            }
        });
    }
}
