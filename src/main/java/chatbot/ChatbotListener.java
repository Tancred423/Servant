package chatbot;

import moderation.guild.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import patreon.PatreonHandler;
import servant.Log;
import servant.Servant;
import zChatLib.Chat;
import zChatLib.MagicBooleans;

import java.sql.SQLException;

public class ChatbotListener extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        // Enabled?
        try {
            if (event.getGuild() != null) if (!new Guild(event.getGuild().getIdLong()).getToggleStatus("baguette")) return;
        } catch (SQLException e) {
            new Log(e, event, "chatbot").sendLogSqlMessageReceivedEvent(false);
        }

        // Do not react to other bots or yourself.
        User author = event.getAuthor();
        if (author.isBot()) return;

        Message message = event.getMessage();
        String contentDisplay = message.getContentDisplay();
        contentDisplay = contentDisplay.replaceFirst("@" + event.getJDA().getSelfUser().getName(), "").trim();

        // Chatbot is handles differently in DM.
        boolean isDM = event.getGuild() == null;

        // Do not interact with commands.
        if (contentDisplay.startsWith(Servant.config.getDefaultPrefix())) return;

        if (isDM) {
            // Interact
            interact(event, contentDisplay);
        } else {
            // React if there is a {botName}_talk text channel.
            if (event.getChannel().getName().toLowerCase().equals(event.getJDA().getSelfUser().getName().toLowerCase() + "-talk")) {
                interact(event, contentDisplay);
                return;
            }

            // Or if bot got mentioned.
            if (message.getMentionedMembers().isEmpty()) return;
            User mentioned = message.getMentionedMembers().get(0).getUser();
            if (mentioned.getIdLong() != event.getJDA().getSelfUser().getIdLong()) return;
            interact(event, contentDisplay);
        }
    }

    private static void interact(MessageReceivedEvent event, String content) {
        // Has to be $3 Patron.
        if (!PatreonHandler.is$10Patron(event.getAuthor())) {
            PatreonHandler.sendWarning(event.getChannel(), "$3");
            return;
        }

        MagicBooleans.trace_mode = false;

        zChatLib.Chat chatSession = new Chat(Servant.chatBot);
        Servant.chatBot.brain.nodeStats();

        String response = chatSession.multisentenceRespond(content);
        response = response.replaceAll("&lt;", "<");
        response = response.replaceAll("&gt;", ">");

        event.getChannel().sendMessage(response).queue();
    }
}
