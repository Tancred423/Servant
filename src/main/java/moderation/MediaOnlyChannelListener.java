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

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MediaOnlyChannelListener extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        User author = event.getAuthor();
        if (author.isBot()) return;

        // Enabled?
        try {
            if (!new servant.Guild(event.getGuild().getIdLong()).getToggleStatus("mediaonlychannel")) return;
        } catch (SQLException e) {
            new Log(e, event, "mediaonlychannel").sendLogSqlReceiveEvent(false);
        }

        // Is message in mo-channel?
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        servant.Guild internalGuild;
        try {
            internalGuild = new servant.Guild(guild.getIdLong());
        } catch (SQLException e) {
            new Log(e, event, "mediaonlychannel").sendLogSqlCommandEvent(true);
            return;
        }
        try {
            if (internalGuild.mediaOnlyChannelHasEntry(channel)) {
                // Is is an url link or a file?
                boolean validMessage = true;
                Message message = event.getMessage();

                List<Message.Attachment> attachments = message.getAttachments();
                if (!attachments.isEmpty()) {
                    for (Message.Attachment attachment : attachments) {
                        String[] splitName = attachment.getFileName().split("\\.");
                        if (!isValidExtension(splitName[splitName.length - 1])) validMessage = false;
                    }
                } else {
                    String url = null;
                    String args[] = event.getMessage().getContentDisplay().split(" ");
                    for (String arg : args) if (arg.startsWith("http")) url = arg;

                    if (url == null) validMessage = false;
                    else if (!isValidUrl(url)) validMessage = false;
                }

                if (!validMessage) {
                    // No files nor valid url. -> Delete and inform about fo-channel.
                    event.getMessage().delete().queue();
                    MessageBuilder mb = new MessageBuilder();
                    mb.setContent(author.getAsMention() + ", this is a media only channel!\n" +
                            "You are allowed to:\n" +
                            "- Send images or videos with any description.\n" +
                            "- Post a valid image or video url link with any description.\n" +
                            "*This message will be deleted in 30 seconds.*");
                    new MessageHandler().sendAndExpire(channel, mb.build(), 30 * 1000); // 30 seconds.
                }
            }
        } catch (SQLException e) {
            new Log(e, event, "mediaonlychannellistener").sendLogSqlReceiveEvent(false);
        }
    }

    private static boolean isValidUrl(String urlString) {
        // Check for valid url.
        URL url;
        URLConnection c;
        try {
            url = new URL(urlString);
            c = url.openConnection();
        } catch (IOException e) {
            return false;
        }

        // Check for content type
        String contentType = c.getContentType();
        return contentType.startsWith("image/")
                || contentType.startsWith("video/")
                || contentType.startsWith("audio/")
                || urlString.contains("youtube.com/watch")
                || urlString.contains("soundcloud.com");
    }

    private static boolean isValidExtension(String extension) {
        List<String> extensionList = new ArrayList<>();
        // Image
        extensionList.add("jpg");
        extensionList.add("jpeg");
        extensionList.add("png");
        extensionList.add("gif");
        extensionList.add("webp");
        extensionList.add("tiff");
        extensionList.add("bmp");
        extensionList.add("svg");
        // Video
        extensionList.add("mp4");
        extensionList.add("3gp");
        extensionList.add("ogg");
        extensionList.add("wmv");
        extensionList.add("webm");
        extensionList.add("flv");
        extensionList.add("avi");
        extensionList.add("mov");
        // Audio
        extensionList.add("wav");
        extensionList.add("mp3");
        extensionList.add("flac");
        extensionList.add("aiff");
        extensionList.add("pcm");
        extensionList.add("aac");

        return extensionList.contains(extension.toLowerCase());
    }
}