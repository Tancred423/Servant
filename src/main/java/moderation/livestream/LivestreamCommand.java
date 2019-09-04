// Author: Tancred423 (https://github.com/Tancred423)
package moderation.livestream;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.guild.GuildHandler;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import moderation.user.User;
import utilities.Constants;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.List;

public class LivestreamCommand extends Command {
    public LivestreamCommand() {
        this.name = "livestream";
        this.aliases = new String[]{"stream"};
        this.help = "Livestream notifications.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = Constants.MOD_COOLDOWN;
        this.cooldownScope = CooldownScope.GUILD;
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL, Permission.MANAGE_ROLES};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MENTION_EVERYONE, Permission.MANAGE_ROLES};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Toggle.isEnabled(event, name)) return;

        var lang = LanguageHandler.getLanguage(event, name);
        var p = GuildHandler.getPrefix(event, name);

        String arg = event.getArgs();
        if (arg.isEmpty()) {
            try {
                var description = LanguageHandler.get(lang, "livestream_description");
                var usage = String.format(LanguageHandler.get(lang, "livestream_usage"),
                        p, name, p, name, p, name, p, name, p, name, p, name, p, name, p, name);
                var hint = LanguageHandler.get(lang, "livestream_hint");
                event.reply(new UsageEmbed(name, event.getAuthor(), description, ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            }
            return;
        }

        try {
            var message = event.getMessage();
            var guild = event.getGuild();
            var internalGuild = new moderation.guild.Guild(guild.getIdLong());
            var args = arg.split(" ");
            switch (args[0].toLowerCase()) {
                case "toggle":
                case "t":
                    internalGuild.toggleStreamerMode();
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
                        internalGuild.setStreamChannel(message.getMentionedChannels().get(0));
                        event.reactSuccess();
                    } else if (message.getMentionedChannels().isEmpty() && message.getMentionedRoles().isEmpty()){
                        internalGuild.setStreamer(message.getMentionedMembers().get(0).getUser().getIdLong());
                        event.reactSuccess();
                    } else if (message.getMentionedMembers().isEmpty() && message.getMentionedChannels().isEmpty()){
                        internalGuild.setStreamingRole(message.getMentionedRoles().get(0).getIdLong());
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
                        if (internalGuild.unsetStreamChannel()) event.reactSuccess();
                        else {
                            event.reply(LanguageHandler.get(lang, "livestream_nochannel"));
                            event.reactWarning();
                        }
                    } else if (message.getMentionedChannels().isEmpty() && message.getMentionedRoles().isEmpty()) {
                        if (internalGuild.unsetStreamer(message.getMentionedMembers().get(0).getUser().getIdLong()))
                            event.reactSuccess();
                        else {
                            event.reply(LanguageHandler.get(lang, "livestream_nostreamer"));
                            event.reactWarning();
                        }
                    } else if (message.getMentionedMembers().isEmpty() && message.getMentionedChannels().isEmpty()) {
                        if (internalGuild.unsetStreamingRole())
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
                    var internalUser = new User(event.getAuthor().getIdLong());
                    var eb = new EmbedBuilder();
                    var sb = new StringBuilder();
                    List<Long> streamers = internalGuild.getStreamers();

                    long roleId = internalGuild.getStreamingRoleId();

                    long channelId = internalGuild.getStreamChannelId();
                    for (Long streamer : streamers)
                        sb.append(guild.getMemberById(streamer).getAsMention()).append("\n");

                    boolean streamerMode = internalGuild.isStreamerMode();

                    eb.setColor(internalUser.getColor());
                    eb.setAuthor(LanguageHandler.get(lang, "livestream_settings"), null, guild.getIconUrl());
                    eb.addField(LanguageHandler.get(lang, "livestream_notificationchannel"),
                            (channelId == 0 ? LanguageHandler.get(lang, "livestream_nochannelset") : guild.getTextChannelById(channelId).getAsMention()), true);
                    eb.addField(LanguageHandler.get(lang, "livestream_role"),
                            (roleId == 0 ? LanguageHandler.get(lang, "livestream_noroleset") : guild.getRoleById(roleId).getAsMention()), true);
                    eb.addField(LanguageHandler.get(lang, "livestream_mode"),
                            (streamerMode ? LanguageHandler.get(lang, "livestream_streamermode") : LanguageHandler.get(lang, "livestream_publicmode")), true);
                    eb.addField(LanguageHandler.get(lang, "livestream_streamers"),
                            (streamers.isEmpty() ? LanguageHandler.get(lang, "livestream_nostreamersset") : sb.toString()), false);

                    event.reply(eb.build());
                    break;

                default:
                    event.reactError();
                    event.reply(LanguageHandler.get(lang, "livestream_firstarg"));
                    break;
            }
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(true);
            return;
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
