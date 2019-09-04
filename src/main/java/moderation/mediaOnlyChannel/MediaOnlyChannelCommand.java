// Author: Tancred423 (https://github.com/Tancred423)
package moderation.mediaOnlyChannel;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import moderation.user.User;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import servant.Log;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.List;

public class MediaOnlyChannelCommand extends Command {
    public MediaOnlyChannelCommand() {
        this.name = "mediaonlychannel";
        this.aliases = new String[]{"mediaonly"};
        this.help = "Files and links only channels.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;

        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        var guild = event.getGuild();
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());

        if (event.getArgs().isEmpty()) {
            try {
                var description = LanguageHandler.get(lang, "mediaonlychannel_description");
                var usage = String.format(LanguageHandler.get(lang, "mediaonlychannel_usage"),
                        p, name, p, name, p, name, p, name, p, name);
                var hint = LanguageHandler.get(lang, "mediaonlychannel_hint");
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
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

                try {
                    internalGuild.addMediaOnlyChannel(channel);
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }

                event.reactSuccess();
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

                boolean wasUnset;
                try {
                    wasUnset = internalGuild.unsetMediaOnlyChannel(channel);
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }

                if (wasUnset) event.reactSuccess();
                else event.reply(LanguageHandler.get(lang, "mediaonlychannel_unset_fail"));
                break;

            case "show":
            case "sh":
                List<MessageChannel> channels;
                try {
                    channels = internalGuild.getMediaOnlyChannels();
                } catch (SQLException e) {
                    new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
                    return;
                }
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

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new moderation.guild.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }
    }
}
