package moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.Error;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import servant.Log;

import java.sql.SQLException;
import java.util.List;

@CommandInfo(
        name = {"FileOnlyChannel"},
        description = "Set up channels where only files and links can be posted!",
        usage = "fileonly [set|unset] #channel",
        requirements = {"The bot has all required permissions."}
)
@Error(
        value = "If arguments are provided, but they are not a mention.",
        response = "[Argument] is not a valid channel mention!"
)
@RequiredPermissions({Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE})
@Author("Tancred")
public class FileOnlyChannelCommand extends Command {
    public FileOnlyChannelCommand() {
        this.name = "fileonlychannel";
        this.aliases = new String[]{"fileonly", "foc", "fo"};
        this.help = "set up channels where only files and links can be posted";
        this.category = new Category("Moderation");
        this.arguments = "[set|unset|show] #channel";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE};
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.reply("You provided no arguments.\n" +
                    "You have to `set`, `unset` or `show`.");
            return;
        }

        String[] args = event.getArgs().split(" ");

        Guild guild = event.getGuild();
        servant.Guild internalGuild = new servant.Guild(guild.getIdLong());
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
                    internalGuild.addFileOnlyChannel(channel);
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }

                ((TextChannel) channel).getManager().setTopic("‼ File-only channel ‼").queue();

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
                    wasUnset = internalGuild.unsetFileOnlyChannel(channel);
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }

                if (wasUnset) {
                    ((TextChannel) channel).getManager().setTopic("").queue();
                    event.reactSuccess();
                }
                else event.reply("This channel was not set as an file only channel.");
                break;

            case "show":
            case "sh":
                List<MessageChannel> channels;
                try {
                    channels = internalGuild.getFileOnlyChannels();
                } catch (SQLException e) {
                    new Log(e, event, name).sendLogSqlCommandEvent(true);
                    return;
                }
                if (channels == null) event.reply("There are not file only channels.");
                else {
                    StringBuilder sb = new StringBuilder();
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
            new servant.Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
