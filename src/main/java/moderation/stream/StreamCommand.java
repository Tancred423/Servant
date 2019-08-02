// Author: Tancred423 (https://github.com/Tancred423)
package moderation.stream;

import moderation.guild.Guild;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import servant.Log;
import servant.User;
import utilities.UsageEmbed;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.Command;
import zJdaUtilsLib.com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;
import java.util.List;

public class StreamCommand extends Command {
    public StreamCommand() {
        this.name = "stream";
        this.aliases = new String[]{"streamer", "twitch"};
        this.help = "Livestream notifications.";
        this.category = new Category("Moderation");
        this.arguments = null;
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 0;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[]{Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("stream")) return;
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), name, event).sendLog(false);
        }

        String arg = event.getArgs();
        // Usage
        if (arg.isEmpty()) {
            try {
                var prefix = new Guild(event.getGuild().getIdLong()).getPrefix();

                var description = "You can set up streamers, one stream notification channel and one streamer role.\n" +
                        "Once a streamer goes online, a notification **with** @everyone will be posted and the streamer will receive the set role.\n" +
                        "If you toggle the streamer mode `off`, a notification **without** @everyone will be posted and the member will receive the set role.";

                var usage = "**(Un)setting a streamer**\n" +
                        "Set: `" + prefix + name + " set @user`\n" +
                        "Unset: `" + prefix + name + " unset @user`\n" +
                        "\n" +
                        "**(Un)setting the notification channel**\n" +
                        "Set: `" + prefix + name + " set #channel`\n" +
                        "Unset: `" + prefix + name + " unset #channel`\n" +
                        "\n" +
                        "**(Un)settings the streaming role**\n" +
                        "Set: `" + prefix + name + " set @role`\n" +
                        "Unset: `" + prefix + name + " unset @role`\n" +
                        "\n" +
                        "**Toggle streamer mode**\n" +
                        "Command: `" + prefix + name + " toggle`\n" +
                        "\n" +
                        "**Showing current stream settings**\n" +
                        "Command: `" + prefix + name + " show`";

                var hint = "There can be multiple streamers but only one notification channel.";

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
                        event.reply("You didn't mention a channel, user nor role.");
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
                        event.reply("You mentioned too much. One at a time!");
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
                        event.reply("You didn't mention a channel, user nor role.");
                        event.reactError();
                    } else if (message.getMentionedMembers().isEmpty() && message.getMentionedRoles().isEmpty()) {
                        if (internalGuild.unsetStreamChannel()) event.reactSuccess();
                        else {
                            event.reply("There was no channel set.");
                            event.reactWarning();
                        }
                    } else if (message.getMentionedChannels().isEmpty() && message.getMentionedRoles().isEmpty()) {
                        if (internalGuild.unsetStreamer(message.getMentionedMembers().get(0).getUser().getIdLong()))
                            event.reactSuccess();
                        else {
                            event.reply("This user is not a streamer.");
                            event.reactWarning();
                        }
                    } else if (message.getMentionedMembers().isEmpty() && message.getMentionedChannels().isEmpty()) {
                        if (internalGuild.unsetStreamingRole())
                            event.reactSuccess();
                        else {
                            event.reply("This role was not set.");
                            event.reactWarning();
                        }
                    } else {
                        event.reply("You mentioned too much. One at a time!");
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

                    eb.setColor(internalUser.getColor());
                    eb.setAuthor("Stream Settings", null, guild.getIconUrl());
                    eb.addField("Notification Channel", (channelId == 0 ? "No channel set." : guild.getTextChannelById(channelId).getAsMention()), false);
                    eb.addField("Streaming Role", (roleId == 0 ? "No role set." : guild.getRoleById(roleId).getAsMention()), false);
                    eb.addField("Streamers", (streamers.isEmpty() ? "No streamers set." : sb.toString()), false);

                    event.reply(eb.build());
                    break;

                default:
                    event.reactError();
                    event.reply("Either `set`, `unset` or `show`");
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
