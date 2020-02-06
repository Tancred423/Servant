// Author: Tancred423 (https://github.com/Tancred423)
package moderation;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.guild.Server;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import utilities.Constants;
import utilities.MessageUtil;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class MediaOnlyChannelCommand extends Command {
    public MediaOnlyChannelCommand() {
        this.name = "mediaonlychannel";
        this.aliases = new String[] { "mediaonly" };
        this.help = "Files and links only channels.";
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
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        var guild = event.getGuild();
        var server = new Server(guild);
        var user = event.getAuthor();

        if (event.getArgs().isEmpty()) {
            var description = LanguageHandler.get(lang, "mediaonlychannel_description");
            var usage = String.format(LanguageHandler.get(lang, "mediaonlychannel_usage"),
                    p, name, p, name, p, name, p, name, p, name);
            var hint = LanguageHandler.get(lang, "mediaonlychannel_hint");
            event.reply(MessageUtil.createUsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint));
            return;
        }

        var args = event.getArgs().split(" ");
        MessageChannel channel;

        switch (args[0].toLowerCase()) {
            case "set":
            case "s":
                if (args.length < 2) {
                    event.reply(LanguageHandler.get(lang, "mediaonlychannel_missingmention"));
                    return;
                }

                try {
                    channel = event.getMessage().getMentionedChannels().get(0);
                } catch (IndexOutOfBoundsException e) {
                    event.reply(LanguageHandler.get(lang, "mediaonlychannel_invalidchannel"));
                    return;
                }

                if (server.setMediaOnlyChannel(channel)) event.reactSuccess();
                else event.replyError(LanguageHandler.get(lang, "mediaonlychannel_alreadyset"));
                break;

            case "unset":
            case "u":
                if (args.length < 2) {
                    event.reply(LanguageHandler.get(lang, "mediaonlychannel_missingmention"));
                    return;
                }

                try {
                    channel = event.getMessage().getMentionedChannels().get(0);
                } catch (IndexOutOfBoundsException e) {
                    event.reply(LanguageHandler.get(lang, "mediaonlychannel_invalidchannel"));
                    return;
                }

                if (server.unsetMediaOnlyChannel(channel)) event.reactSuccess();
                else event.reply(LanguageHandler.get(lang, "mediaonlychannel_unset_fail"));
                break;

            case "show":
            case "sh":
                var channels = server.getMediaOnlyChannels();
                if (channels == null) event.reply(LanguageHandler.get(lang, "mediaonlychannel_nochannels"));
                else {
                    var sb = new StringBuilder();
                    for (var chan : channels)
                        sb.append(chan.getName()).append(" (").append(chan.getIdLong()).append(")\n");
                    event.reply(sb.toString());
                }
                break;

            default:
                event.reply(LanguageHandler.get(lang, "mediaonlychannel_firstarg"));
        }
    }
}
