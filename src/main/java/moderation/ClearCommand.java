package moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.Error;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;
import servant.Guild;
import servant.Log;
import servant.User;
import utilities.MessageHandler;
import utilities.Parser;

import java.sql.SQLException;
import java.util.List;

@CommandInfo(
        name = {"Clear", "Clean"},
        description = "Delete up to 100 messages.",
        usage = "moderation [1 - 100]",
        requirements = {"The bot is allowed to delete messages (Manage Messages)."}
)
@Error(
        value = "If arguments are provided, but they are not a integer.",
        response = "[Argument] is not a valid integer!"
)
@RequiredPermissions({Permission.MESSAGE_MANAGE})
@Author("Tancred")
public class ClearCommand extends Command {
    public ClearCommand() {
        this.name = "clear";
        this.aliases = new String[]{"clean"};
        this.help = "delete messages";
        this.category = new Category("Moderation");
        this.arguments = "[1 - 100]";
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String arg = event.getArgs();

        if (arg.isEmpty()) {
            event.reply("You have to declare how many messages you want to delete. (1 - 100)");
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
