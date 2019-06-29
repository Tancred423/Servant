package moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;
import servant.Guild;
import servant.Log;
import servant.Servant;
import servant.User;
import utilities.MessageHandler;
import utilities.Parser;
import utilities.UsageEmbed;

import java.sql.SQLException;
import java.util.List;

public class ClearCommand extends Command {
    public ClearCommand() {
        this.name = "clear";
        this.aliases = new String[]{"clean", "delete"};
        this.help = "delete messages | **MANAGE MESSAGES**";
        this.category = new Category("Moderation");
        this.arguments = "[1 - 100]";
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        // Enabled?
        try {
            if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("clear")) return;
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }

        String arg = event.getArgs();
        String prefix = Servant.config.getDefaultPrefix();
        // Usage
        if (arg.isEmpty()) {
            try {
                String usage = "**Delete some messages**\n" +
                        "Command: `" + prefix + name + " [1 - 100]`\n" +
                        "Example: `" + prefix + name + " 50`";

                String hint = "The range is inclusively, so you can also delete just 1 or a total of 100 messages.";

                event.reply(new UsageEmbed(name, event.getAuthor(), ownerCommand, userPermissions, aliases, usage, hint).getEmbed());
            } catch (SQLException e) {
                new Log(e, event, name).sendLogSqlCommandEvent(true);
            }
            return;
        }

        if (!arg.matches("[0-9]+")) {
            event.reply("You only can put in numbers!");
            return;
        }

        int clearValue = Integer.parseInt(arg);

        if (clearValue < 0 || clearValue > 100) {
            event.reply("Amount has to be between 1 and 100 inclusively.");
            return;
        }

        MessageChannel channel = event.getChannel();

        // Clear messages.
        event.getMessage().delete().complete();
        MessageHistory history = new MessageHistory(channel);
        List<Message> messageList = history.retrievePast(clearValue).complete();
        int actuallyCleared = 0;
        for (Message message : messageList) {
            if (Parser.isOlderThanTwoWeeks(message.getCreationTime())) break;
            message.delete().complete();
            actuallyCleared++;
        }

        new MessageHandler().sendAndExpire(
                channel,
                new MessageBuilder().setContent(actuallyCleared + " messages cleared").build(),
                5 * 1000 // 5 seconds.
        );

        // Statistics.
        try {
            new User(event.getAuthor().getIdLong()).incrementFeatureCount(name.toLowerCase());
            new Guild(event.getGuild().getIdLong()).incrementFeatureCount(name.toLowerCase());
        } catch (SQLException e) {
            new Log(e, event, name).sendLogSqlCommandEvent(false);
        }
    }
}
