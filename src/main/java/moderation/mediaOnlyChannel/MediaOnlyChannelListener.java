package moderation.mediaOnlyChannel;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import servant.Log;
import utilities.MessageHandler;
import utilities.Parser;

import java.sql.SQLException;
import java.util.List;

public class MediaOnlyChannelListener extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        var author = event.getAuthor();
        if (author.isBot()) return;

        // Enabled?
        try {
            if (!new moderation.guild.Guild(event.getGuild().getIdLong()).getToggleStatus("mediaonlychannel")) return;
        } catch (SQLException e) {
            new Log(e, event, "mediaonlychannel").sendLogSqlGuildMessageReceivedEvent(false);
        }

        // Is message in mo-channel?
        var channel = event.getChannel();
        var guild = event.getGuild();
        moderation.guild.Guild internalGuild;
        try {
            internalGuild = new moderation.guild.Guild(guild.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, "mediaonlychannel").sendLogSqlCommandEvent(true);
            return;
        }
        try {
            if (internalGuild.mediaOnlyChannelHasEntry(channel)) {
                // Has to have an attachment or a valid url.
                var validMessage = true;
                var message = event.getMessage();

                List<Message.Attachment> attachments = message.getAttachments();
                if (attachments.isEmpty()) {
                    String url = null;
                    var args = event.getMessage().getContentDisplay().split(" ");
                    for (var arg : args) if (arg.startsWith("http")) url = arg;

                    if (url == null) validMessage = false;
                    else if (!Parser.isValidUrl(url)) validMessage = false;
                }

                if (!validMessage) {
                    // No files nor valid url. -> Delete and inform about mo-channel.
                    event.getMessage().delete().queue();
                    var mb = new MessageBuilder();
                    mb.setContent(author.getAsMention() + ", this is a media only channel!\n" +
                            "You are allowed to:\n" +
                            "- Send upload files with an optional description.\n" +
                            "- Post a valid url with an optional description.\n" +
                            "*This message will be deleted in 30 seconds.*");
                    new MessageHandler().sendAndExpire(channel, mb.build(), 30 * 1000); // 30 seconds.
                }
            }
        } catch (SQLException e) {
            new Log(e, event, "mediaonlychannellistener").sendLogSqlGuildMessageReceivedEvent(false);
        }
    }
}
