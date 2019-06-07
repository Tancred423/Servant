package moderation;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import utilities.MessageHandler;

import java.net.URL;
import java.sql.SQLException;

public class FileOnlyChannelListener extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        User author = event.getAuthor();

        if (author.isBot()) return;

        // Is message in fo-channel?
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        servant.Guild internalGuild = new servant.Guild(guild.getIdLong());
        try {
            if (internalGuild.fileonlychannelHasEntry(channel)) {
                // Is is an url link or a file?
                Message message = event.getMessage();
                if (message.getAttachments().isEmpty()
                        && !isValidUrl(message.getContentDisplay().split(" ")[0])) {
                    // No files nor valid url. -> Delete and inform about fo-channel.
                    event.getMessage().delete().queue();
                    MessageBuilder mb = new MessageBuilder();
                    mb.setContent(author.getAsMention() + ", this is a file-only channel!\n" +
                            "You are allowed to:\n" +
                            "- Send any files with any description.\n" +
                            "- Post a valid url link and an optional description **behind** this url.\n" +
                            "*This message will be deleted in 30 seconds.*");
                    new MessageHandler().sendAndExpire(channel, mb.build(), 30 * 1000); // 30 seconds.
                }
            }
        } catch (SQLException e) {
            new Log(e, event, "fileonlychannellistener").sendLogSqlReceiveEvent(false);
        }
    }

    private static boolean isValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
