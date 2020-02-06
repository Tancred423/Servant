// Author: Tancred423 (https://github.com/Tancred423)
package moderation.birthday;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.guild.Server;
import net.dv8tion.jda.api.Permission;
import servant.Log;
import utilities.Constants;
import utilities.MessageUtil;
import utilities.Parser;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.text.ParseException;

public class BirthdayCommand extends Command {
    public BirthdayCommand() {
        this.name = "birthday";
        this.aliases = new String[] { "bday" };
        this.help = "Birthdays of server members.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[]{};
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        if (event.getArgs().isEmpty()) {
            var description = String.format(LanguageHandler.get(lang, "birthday_description"), event.getJDA().getSelfUser().getName(), event.getJDA().getSelfUser().getName());
            var usage = String.format(LanguageHandler.get(lang, "birthday_usage"),
                    p, name, p, name, p, name, p, name, p, name, p, name, p, name, event.getJDA().getSelfUser().getName(), p, name, event.getJDA().getSelfUser().getName());
            var hint = LanguageHandler.get(lang, "birthday_hint");
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint));
            return;
        }

        var message = event.getMessage();
        var args = event.getArgs();
        var guild = event.getGuild();
        var server = new Server(guild);
        var bot = event.getSelfUser();

        try {
            if (!message.getMentionedChannels().isEmpty()) {
                // Mod - Set Notification Channel
                if (event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                    server.setBirthdayChannelId(message.getMentionedChannels().get(0).getIdLong());
                    event.reactSuccess();
                } else
                    event.reply(String.format(LanguageHandler.get(lang, "permission"), Permission.MANAGE_CHANNEL.getName()));
            } else if (args.equalsIgnoreCase("unsetchannel")) {
                // Mod - Unset Notification Channel
                if (event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                    server.unsetBirthdayChannelId();
                    event.reactSuccess();
                } else
                    event.reply(String.format(LanguageHandler.get(lang, "permission"), Permission.MANAGE_CHANNEL.getName()));
            } else if (args.equalsIgnoreCase("updatelist")) {
                // Mod - Create Auto Update List
                if (event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                    message.delete().queue();
                    BirthdayHandler.sendList(true, event);
                } else
                    event.reply(String.format(LanguageHandler.get(lang, "permission"), Permission.MANAGE_CHANNEL.getName()));
            } else if (Parser.isValidDate(args)) {
                // User - Add Birthday
                server.setBirthday(event.getAuthor().getIdLong(), args);
                event.reactSuccess();
            } else if (args.equalsIgnoreCase("unsetbirthday") || args.equalsIgnoreCase("unsetbday")) {
                // User - Remove Birthday
                if (server.unsetBirthday(event.getAuthor().getIdLong()))
                    event.reactSuccess();
                else {
                    event.reply(LanguageHandler.get(lang, "birthday_not_set"));
                    event.reactWarning();
                }
                event.reactSuccess();
            } else if (args.equalsIgnoreCase("list")) {
                // User - One Time List
                BirthdayHandler.sendList(false, event);
            } else if (args.equalsIgnoreCase(bot.getName())) {
                // Mod - Add or remove bot's birthday
                if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                    if (server.getBirthdays().containsKey(bot.getIdLong()))
                        server.unsetBirthday(bot.getIdLong());
                    else server.setBirthday(bot.getIdLong(), "2018-04-06");
                    event.reactSuccess();
                } else
                    event.reply(String.format(LanguageHandler.get(lang, "permission"), Permission.MESSAGE_MANAGE.getName()));
            } else {
                event.reply(LanguageHandler.get(lang, "birthday_invalid"));
            }
        } catch (ParseException e) {
            new Log(e, guild, event.getAuthor(), name, event).sendLog(true);
        }
    }
}
