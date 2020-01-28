// Author: Tancred423 (https://github.com/Tancred423)
package moderation.joinleave;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.guild.Server;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class LeaveCommand extends Command {
    public LeaveCommand() {
        this.name = "leave";
        this.aliases = new String[0];
        this.help = "Alert for leaving user.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[] { Permission.MANAGE_CHANNEL };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);
        var guild = event.getGuild();
        var server = new Server(guild);

        // Usage
        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "leave_description");
            var usage = String.format(LanguageHandler.get(lang, "leave_usage"), p, name, p, name, p, name, p, name);
            event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, null).getEmbed());
            return;
        }

        var args = event.getArgs().split(" ");
        MessageChannel channel;

        switch (args[0].toLowerCase()) {
            case "set":
            case "s":
                if (args.length < 2) {
                    event.reply(LanguageHandler.get(lang, "joinleave_nochannel_mention"));
                    return;
                }

                channel = event.getMessage().getMentionedChannels().get(0);

                server.setLeaveNotifierChannel(channel);
                event.reactSuccess();
                break;

            case "unset":
            case "u":
                boolean wasUnset;
                wasUnset = server.unsetLeaveNotifierChannel();
                if (wasUnset) event.reactSuccess();
                else event.reply(LanguageHandler.get(lang, "joinleave_unset_fail"));
                break;

            case "show":
            case "sh":
                channel = server.getLeaveNotifierChannel();
                if (channel == null) event.reply(LanguageHandler.get(lang, "joinleave_nochannel_set"));
                else
                    event.reply(String.format(LanguageHandler.get(lang, "joinleave_current"), channel.getName()));
                break;

            default:
                event.reply(LanguageHandler.get(lang, "joinleave_firstarg"));
                event.reactError();
                break;
        }
    }
}
