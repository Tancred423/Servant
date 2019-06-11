package moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import servant.Log;

import java.sql.SQLException;

@CommandInfo(
        name = {"Join", "Leave"},
        description = "Set up a channel for join and leave messages!",
        usage = "join [set|unset|status] [on set: #channel]"
)
@Author("Tancred")
public class JoinCommand extends Command {
    public JoinCommand() {
        this.name = "join";
        this.aliases = new String[]{"leave"};
        this.help = "set up a channel for join and leave messages";
        this.category = new Category("Moderation");
        this.arguments = "[set|unset|status] [on set: #channel]";
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");

        if (args.length < 1) {
            event.reply("No arguments!\n" +
                    "`join [set|unset|status] [on set: #channel]`\n" +
                    "e.g. `join set #channel`");
            return;
        }

        Guild guild = event.getGuild();
        MessageChannel channel;
        servant.Guild internalGuild = new servant.Guild(guild.getIdLong());

        switch (args[0].toLowerCase()) {
            case "set":
            case "s":
                if (args.length < 2) {
                    event.reply("No channel was mentioned.");
                    return;
                }

                channel = guild.getTextChannelById(args[1]);

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
    }
}
