package moderation.joinLeaveNotify;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import servant.Log;
import servant.Servant;
import utilities.UsageEmbed;

import java.sql.SQLException;

public class JoinLeaveNotifyCommand extends Command {
    public JoinLeaveNotifyCommand() {
        this.name = "join";
        this.aliases = new String[]{"leave"};
        this.help = "set up a channel for join and leave messages | Manage Channels";
        this.category = new Category("Moderation");
        this.arguments = "[set|unset|status] <on set: #channel>";
        this.hidden = false;
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[0];
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus("join")) return;
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
                var usage = "**Setting up a join and leave notification channel**\n" +
                        "Command: `" + prefix + name + " set [#channel]`\n" +
                        "Example: `" + prefix + name + " set #welcome`\n" +
                        "\n" +
                        "**Unsetting this channel**\n" +
                        "Command: `" + prefix + name + " unset`\n" +
                        "\n" +
                        "**Showing current notification channel**\n" +
                        "Command: `" + prefix + name + " show`";

                var hint = "Shows a message like \"Name#1234 just joined GuildName!\"\n" +
                        "or \"Name#1234 just left GuildName!\"";

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
                    event.reply("No channel was mentioned.");
                    return;
                }

                channel = event.getMessage().getMentionedChannels().get(0);

                try {
                    internalGuild.setJoinNotifierChannel(channel);
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }
                event.reactSuccess();
                break;

            case "unset":
            case "u":
                boolean wasUnset;
                try {
                    wasUnset = internalGuild.unsetJoinNotifierChannel();
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }
                if (wasUnset) event.reactSuccess();
                else event.reply("No channel was unset.");
                break;

            case "status":
                try {
                    channel = internalGuild.getJoinNotifierChannel();
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }
                if (channel == null) event.reply("No channel is set.");
                else event.reply("Current channel: " + channel.getName());
                break;
        }

        // Statistics.
        try {
            new servant.User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            if (event.getGuild() != null) new moderation.guild.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
