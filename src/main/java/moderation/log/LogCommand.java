// Author: Tancred423 (https://github.com/Tancred423)
package moderation.log;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.MessageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class LogCommand extends Command {
    public LogCommand() {
        this.name = "log";
        this.aliases = new String[0];
        this.help = "Log stuff that happens in your server.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[] { Permission.MANAGE_SERVER };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var arg = event.getArgs();
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        if (arg.isEmpty()) {
            var description = LanguageHandler.get(lang, "log_description");
            var usage = String.format(LanguageHandler.get(lang, "log_usage"), p, name, p, name, p, name, p, name, p, name, p, name);
            var hint = String.format(LanguageHandler.get(lang, "log_hint"), p, name);
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint));
            return;
        }

        var server = new Server(event.getGuild());
        var args = arg.split(" ");
        var message = event.getMessage();

        switch (args[0].toLowerCase()) {
            case "set":
            case "s":
                if (message.getMentionedChannels().size() == 0) {
                    event.replyWarning(LanguageHandler.get(lang, "log_invalidmention"));
                    return;
                }

                server.setLogChannel(message.getMentionedChannels().get(0));
                event.reactSuccess();
                break;

            case "unset":
            case "u":
                if (server.unsetLog()) event.reactSuccess();
                else event.replyWarning(LanguageHandler.get(lang, "log_unset"));
                break;

            case "show":
            case "sh":
                var eb = new EmbedBuilder()
                        .setColor(new Master(event.getAuthor()).getColor())
                        .setTitle(LanguageHandler.get(lang, "log_settings"));

                // Log Channel
                var logChannelId = server.getLogChannelId();
                var logChannel = event.getGuild().getTextChannelById(logChannelId);
                if (logChannel != null) eb.addField(LanguageHandler.get(lang, "log_channel"), logChannel.getAsMention(), false);

                // Events
                var logEvents = server.getLogEvents();
                var sb = new StringBuilder();
                for (var entry : logEvents.entrySet())
                    sb.append(entry.getKey()).append(": ").append(entry.getValue() ? LanguageHandler.get(lang, "log_on") : LanguageHandler.get(lang, "log_off")).append("\n");
                eb.addField(LanguageHandler.get(lang, "log_events"), sb.toString(), false);

                event.reply(eb.build());
                break;

            case "toggle":
            case "t":
                if (args.length > 1) {
                    switch (args[1].toLowerCase()) {
                        case "boost_count":
                        case "member_join":
                        case "member_leave":
                        case "role_add":
                        case "role_remove":
                            server.logToggle(args[1].toLowerCase());
                            event.reactSuccess();
                            break;

                        default:
                            event.replyWarning(String.format(LanguageHandler.get(lang, "log_toggle_invalid_args"), p, name));
                            break;
                    }
                } else event.replyWarning(String.format(LanguageHandler.get(lang, "log_toggle_args"), p, name));
                break;

            default:
                break;
        }
    }
}
