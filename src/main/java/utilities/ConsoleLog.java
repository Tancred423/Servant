package utilities;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class ConsoleLog {
    public static void send(MessageReceivedEvent event, boolean isCustomCommand) {
        System.out.println(
                getTimestamp() +
                (isCustomCommand ? "Custom command" : "Command") + " executed: " + event.getMessage().getContentDisplay() + " | " +
                "Guild: " + (event.isFromGuild() ? event.getGuild().getName() + " (" + event.getGuild().getIdLong() + ")" : "DM") + " | " +
                "User: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getIdLong() + ")"
        );
    }

    public static String getTimestamp() {
        return "[" + OffsetDateTime.now(ZoneId.of(Constants.LOG_OFFSET)).toString().replaceAll("T", " ").substring(0, 19) + "] ";
    }
}
