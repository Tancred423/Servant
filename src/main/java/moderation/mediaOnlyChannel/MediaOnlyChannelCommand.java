package moderation.mediaOnlyChannel;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import servant.Log;
import servant.Servant;
import utilities.UsageEmbed;

import java.sql.SQLException;
import java.util.List;

public class MediaOnlyChannelCommand extends Command {
    public MediaOnlyChannelCommand() {
        this.name = "mediaonlychannel";
        this.aliases = new String[]{"mediaonly", "moc", "mo"};
        this.help = "set up channels where only files and links can be posted | Manage Channels";
        this.category = new Category("Moderation");
        this.arguments = "[set|unset|show] <on (un)set: #channel>";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE};
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus("mediaonlychannel")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        var guild = event.getGuild();
        moderation.guild.Guild internalGuild;
        try {
            internalGuild = new moderation.guild.Guild(guild.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(true);
            return;
        }
        var prefix = Servant.config.getDefaultPrefix();
        // Usage
        if (event.getArgs().isEmpty()) {
            try {
                var usage = "**Setting up an media only channel**\n" +
                        "Command: `" + prefix + name + " set [#channel]`\n" +
                        "Example: `" + prefix + name + " set #images`\n" +
                        "\n" +
                        "**Unsetting an media only channel**\n" +
                        "Command: `" + prefix + name + " unset [#channel]`\n" +
                        "Example: `" + prefix + name + " unset #images`\n" +
                        "\n" +
                        "**Showing current media only channels**\n" +
                        "Command: `" + prefix + name + " show`";

                var hint = "You can have multiple media only channels.";

                event.reply(new UsageEmbed(name, event.getAuthor(), ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
            }
            return;
        }

        var args = event.getArgs().split(" ");
        MessageChannel channel;

        switch (args[0].toLowerCase()) {
            case "set":
            case "s":
                if (args.length < 2) {
                    event.reply("You did not provide a channel mention.");
                    return;
                }

                try {
                    channel = event.getMessage().getMentionedChannels().get(0);
                } catch (IndexOutOfBoundsException e) {
                    event.reply("The given channel is invalid.");
                    return;
                }

                try {
                    internalGuild.addMediaOnlyChannel(channel);
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }

                event.reactSuccess();
                break;

            case "unset":
            case "u":
                if (args.length < 2) {
                    event.reply("You did not provide a channel mention.");
                    return;
                }

                try {
                    channel = event.getMessage().getMentionedChannels().get(0);
                } catch (IndexOutOfBoundsException e) {
                    event.reply("The given channel is invalid.");
                    return;
                }

                boolean wasUnset;
                try {
                    wasUnset = internalGuild.unsetMediaOnlyChannel(channel);
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }

                if (wasUnset) event.reactSuccess();
                else event.reply("This channel was not set as an media only channel.");
                break;

            case "show":
            case "sh":
                List<MessageChannel> channels;
                try {
                    channels = internalGuild.getMediaOnlyChannels();
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }
                if (channels == null) event.reply("There are no media only channels.");
                else {
                    var sb = new StringBuilder();
                    for (MessageChannel chan : channels)
                        sb.append(chan.getName()).append(" (").append(chan.getIdLong()).append(")\n");
                    event.reply(sb.toString());
                }
                break;

            default:
                event.reply("Invalid first argument.\n" +
                        "Either `set`, `unset` or `show`");
        }

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new moderation.guild.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
