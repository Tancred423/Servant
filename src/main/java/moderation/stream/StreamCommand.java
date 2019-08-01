package moderation.stream;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import moderation.guild.Guild;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import servant.Log;
import servant.User;
import utilities.UsageEmbed;

import java.sql.SQLException;
import java.util.List;

public class StreamCommand extends Command {
    public StreamCommand() {
        this.name = "stream";
        this.aliases = new String[]{"streamer", "twitch"};
        this.help = "gives @everyone notification if a streamer goes online | Manage Channels";
        this.category = new Category("Moderation");
        this.arguments = "[set|unset|show] <on (un)set: @user|#channel>";
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
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        String arg = event.getArgs();
        // Usage
        if (arg.isEmpty()) {
            try {
                String prefix = new Guild(event.getGuild().getIdLong()).getPrefix();
                var usage = "**(Un)setting a streamer**\n" +
                        "Set: `" + prefix + name + " set @user`\n" +
                        "Unset: `" + prefix + name + " unset @user`\n" +
                        "\n" +
                        "**(Un)setting the notification channel**\n" +
                        "Set: `" + prefix + name + " set #channel`\n" +
                        "Unset: `" + prefix + name + " unset #channel`\n" +
                        "\n" +
                        "**Showing current stream settings**\n" +
                        "Command: `" + prefix + name + " show`";

                var hint = "There can be multiple streamers but only one notification channel.";

                event.reply(new UsageEmbed(name, event.getAuthor(), ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
            }
            return;
        }

        try {
            var message = event.getMessage();
            var guild = event.getGuild();
            var internalGuild = new moderation.guild.Guild(guild.getIdLong());
            var args = arg.split(" ");
            switch (args[0].toLowerCase()) {
                case "set":
                case "s":
                case "add":
                case "a":
                    if (message.getMentionedChannels().isEmpty() && message.getMentionedMembers().isEmpty()) {
                        event.reply("You didn't mention a channel nor user.");
                        event.reactError();
                    } else if (message.getMentionedMembers().isEmpty()) {
                        internalGuild.setStreamChannel(message.getMentionedChannels().get(0));
                        event.reactSuccess();
                    } else {
                        internalGuild.setStreamer(message.getMentionedMembers().get(0).getUser().getIdLong());
                        event.reactSuccess();
                    }
                    break;

                case "unset":
                case "u":
                case "remove":
                case "r":
                    if (message.getMentionedChannels().isEmpty() && message.getMentionedMembers().isEmpty()) {
                        event.reply("You didn't mention a channel nor user.");
                        event.reactError();
                    } else if (message.getMentionedMembers().isEmpty()) {
                        if (internalGuild.unsetStreamChannel()) event.reactSuccess();
                        else {
                            event.reply("There was no channel set.");
                            event.reactWarning();
                        }
                    } else {
                        if (internalGuild.unsetStreamer(message.getMentionedMembers().get(0).getUser().getIdLong()))
                            event.reactSuccess();
                        else {
                            event.reply("This user is not a streamer.");
                            event.reactWarning();
                        }
                    }
                    break;

                case "show":
                case "sh":
                    var internalUser = new User(event.getAuthor().getIdLong());
                    var eb = new EmbedBuilder();
                    var sb = new StringBuilder();
                    List<Long> streamers = internalGuild.getStreamers();

                    long channelId = internalGuild.getStreamChannel();
                    for (Long streamer : streamers)
                        sb.append(guild.getMemberById(streamer).getAsMention()).append("\n");

                    eb.setColor(internalUser.getColor());
                    eb.setAuthor("Stream Settings", null, guild.getIconUrl());
                    eb.addField("Notification Channel", (channelId == 0 ? "No channel set." : guild.getTextChannelById(channelId).getAsMention()), false);
                    eb.addField("Streamers", (streamers.isEmpty() ? "No streamers set." : sb.toString()), false);

                    event.reply(eb.build());
                    break;

                default:
                    event.reactError();
                    event.reply("Either `set`, `unset` or `show`");
                    break;
            }
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
