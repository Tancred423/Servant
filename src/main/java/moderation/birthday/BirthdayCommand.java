// Author: Tancred423 (https://github.com/Tancred423)
package moderation.birthday;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import owner.blacklist.Blacklist;
import servant.Log;
import servant.Servant;
import moderation.toggle.Toggle;
import utilities.Constants;
import utilities.Parser;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;

public class BirthdayCommand extends Command {
    public BirthdayCommand() {
        this.name = "birthday";
        this.aliases = new String[]{"bday"};
        this.help = "Birthdays of server members.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[]{};
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
                    var description = String.format(LanguageHandler.get(lang, "birthday_description"), Servant.jda.getSelfUser().getName());
                    var usage = String.format(LanguageHandler.get(lang, "birthday_usage"),
                            p, name, p, name, p, name, p, name, p, name, p, name, p, name);
                    var hint = LanguageHandler.get(lang, "birthday_hint");
                    event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                }
                return;
            }

            var message = event.getMessage();
            var args = event.getArgs();
            var guild = event.getGuild();
            var internalGuild = new Guild(guild.getIdLong());
            var bot = event.getSelfUser();

            try {
                if (!message.getMentionedChannels().isEmpty()) {
                    // Mod - Set Notification Channel
                    if (event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                        internalGuild.setBirthdayChannelId(message.getMentionedChannels().get(0).getIdLong());
                        event.reactSuccess();
                    } else event.reply(String.format(LanguageHandler.get(lang, "permission"), Permission.MANAGE_CHANNEL.getName()));
                } else if (args.equalsIgnoreCase("unsetchannel")) {
                    // Mod - Unset Notification Channel
                    if (event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                        internalGuild.unsetBirthdayChannelId();
                        event.reactSuccess();
                    } else event.reply(String.format(LanguageHandler.get(lang, "permission"), Permission.MANAGE_CHANNEL.getName()));
                } else if (args.equalsIgnoreCase("updatelist")) {
                    // Mod - Create Auto Update List
                    if (event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                        message.delete().queue();
                        BirthdayHandler.sendList(true, name, event);
                    } else event.reply(String.format(LanguageHandler.get(lang, "permission"), Permission.MANAGE_CHANNEL.getName()));
                } else if (Parser.isValidDate(args)) {
                    // User - Add Birthday
                    internalGuild.setBirthday(event.getAuthor().getIdLong(), args);
                    event.reactSuccess();
                } else if (args.equalsIgnoreCase("unsetbirthday")) {
                    // User - Remove Birthday
                    if (internalGuild.unsetBirthday(event.getAuthor().getIdLong())) event.reactSuccess();
                    else {
                        event.reply(LanguageHandler.get(lang, "birthday_not_set"));
                        event.reactWarning();
                    }
                    event.reactSuccess();
                } else if (args.equalsIgnoreCase("list")) {
                    // User - One Time List
                    BirthdayHandler.sendList(false, name, event);
                } else if (args.equalsIgnoreCase(bot.getName())) {
                    // Mod - Add or remove bot's birthday
                    if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                        if (internalGuild.getBirthdays().containsKey(bot.getIdLong())) internalGuild.unsetBirthday(bot.getIdLong());
                        else internalGuild.setBirthday(bot.getIdLong(), "2018-04-06");
                        event.reactSuccess();
                    } else event.reply(String.format(LanguageHandler.get(lang, "permission"), Permission.MESSAGE_MANAGE.getName()));
                } else {
                    event.reply(LanguageHandler.get(lang, "birthday_invalid"));
                }
            } catch (SQLException | ParseException e) {
                new Log(e, guild, event.getAuthor(), name, event).sendLog(true);
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
