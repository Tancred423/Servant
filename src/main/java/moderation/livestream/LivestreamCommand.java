// Author: Tancred423 (https://github.com/Tancred423)
package moderation.livestream;

import files.language.LanguageHandler;
import moderation.guild.GuildHandler;
import moderation.guild.Server;
import moderation.user.Master;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

public class LivestreamCommand extends Command {
    public LivestreamCommand() {
        this.name = "livestream";
        this.aliases = new String[] { "stream" };
        this.help = "Livestream notifications.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[] { Permission.MANAGE_CHANNEL, Permission.MANAGE_ROLES };
        this.botPermissions = new Permission[] {
                Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MENTION_EVERYONE, Permission.MANAGE_ROLES
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        var lang = LanguageHandler.getLanguage(event);
        var p = GuildHandler.getPrefix(event);

        var arg = event.getArgs();
        if (arg.isEmpty()) {
            var description = LanguageHandler.get(lang, "livestream_description");
            var usage = String.format(LanguageHandler.get(lang, "livestream_usage"),
                    p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name);
            var hint = LanguageHandler.get(lang, "livestream_hint");
            event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            return;
        }

        var message = event.getMessage();
        var guild = event.getGuild();
        var server = new Server(guild);
        var args = arg.split(" ");
        switch (args[0].toLowerCase()) {
            case "toggle":
            case "t":
                server.toggleStreamerMode();
                event.reactSuccess();
                break;

            case "set":
            case "s":
            case "add":
            case "a":
                if (message.getMentionedChannels().isEmpty()
                        && message.getMentionedMembers().isEmpty()
                        && message.getMentionedRoles().isEmpty()) {
                    event.reply(LanguageHandler.get(lang, "livestream_missingmention"));
                    event.reactError();
                } else if (message.getMentionedMembers().isEmpty() && message.getMentionedRoles().isEmpty()) {
                    server.setStreamChannel(message.getMentionedChannels().get(0));
                    event.reactSuccess();
                } else if (message.getMentionedChannels().isEmpty() && message.getMentionedRoles().isEmpty()) {
                    server.setStreamer(message.getMentionedMembers().get(0).getUser().getIdLong());
                    event.reactSuccess();
                } else if (message.getMentionedMembers().isEmpty() && message.getMentionedChannels().isEmpty()) {
                    server.setStreamingRole(message.getMentionedRoles().get(0).getIdLong());
                    event.reactSuccess();
                } else {
                    event.reply(LanguageHandler.get(lang, "livestream_toomanymentions"));
                    event.reactError();
                }
                break;

            case "unset":
            case "u":
            case "remove":
            case "r":
                if (message.getMentionedChannels().isEmpty()
                        && message.getMentionedMembers().isEmpty()
                        && message.getMentionedRoles().isEmpty()) {
                    event.reply(LanguageHandler.get(lang, "livestream_missingmention"));
                    event.reactError();
                } else if (message.getMentionedMembers().isEmpty() && message.getMentionedRoles().isEmpty()) {
                    if (server.unsetStreamChannel()) event.reactSuccess();
                    else {
                        event.reply(LanguageHandler.get(lang, "livestream_nochannel"));
                        event.reactWarning();
                    }
                } else if (message.getMentionedChannels().isEmpty() && message.getMentionedRoles().isEmpty()) {
                    if (server.unsetStreamer(message.getMentionedMembers().get(0).getUser().getIdLong()))
                        event.reactSuccess();
                    else {
                        event.reply(LanguageHandler.get(lang, "livestream_nostreamer"));
                        event.reactWarning();
                    }
                } else if (message.getMentionedMembers().isEmpty() && message.getMentionedChannels().isEmpty()) {
                    if (server.unsetStreamingRole())
                        event.reactSuccess();
                    else {
                        event.reply(LanguageHandler.get(lang, "livestream_norole"));
                        event.reactWarning();
                    }
                } else {
                    event.reply(LanguageHandler.get(lang, "livestream_toomanymentions"));
                    event.reactError();
                }
                break;

            case "show":
            case "sh":
                var master = new Master(event.getAuthor());
                var eb = new EmbedBuilder();
                var sb = new StringBuilder();
                var streamers = server.getStreamers();

                var roleId = server.getStreamingRoleId();

                var channelId = server.getStreamChannelId();
                for (Long streamer : streamers) {
                    var streamerMember = guild.getMemberById(streamer);
                    if (streamerMember != null)
                        sb.append(streamerMember.getAsMention()).append("\n");
                }

                var streamerMode = server.isStreamerMode();

                eb.setColor(master.getColor());
                eb.setAuthor(LanguageHandler.get(lang, "livestream_settings"), null, guild.getIconUrl());
                var tc = guild.getTextChannelById(channelId);
                eb.addField(LanguageHandler.get(lang, "livestream_notificationchannel"),
                        (tc == null ? LanguageHandler.get(lang, "livestream_nochannelset") : tc.getAsMention()), true);
                var role = guild.getRoleById(roleId);
                eb.addField(LanguageHandler.get(lang, "livestream_role"),
                        (role == null ? LanguageHandler.get(lang, "livestream_noroleset") : role.getAsMention()), true);
                eb.addField(LanguageHandler.get(lang, "livestream_mode"),
                        (streamerMode ? LanguageHandler.get(lang, "livestream_mode") : LanguageHandler.get(lang, "livestream_publicmode")), true);
                eb.addField(LanguageHandler.get(lang, "livestream_streamers"),
                        (streamers.isEmpty() ? LanguageHandler.get(lang, "livestream_nostreamersset") : sb.toString()), false);

                event.reply(eb.build());
                break;

            default:
                event.reactError();
                event.reply(LanguageHandler.get(lang, "livestream_firstarg"));
                break;
        }
    }
}
