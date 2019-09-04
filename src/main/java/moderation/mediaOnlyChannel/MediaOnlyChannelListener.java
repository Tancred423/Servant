// Author: Tancred423 (https://github.com/Tancred423)
package moderation.mediaOnlyChannel;

import files.language.LanguageHandler;
import moderation.guild.Guild;
import moderation.toggle.Toggle;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
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

        if (!Toggle.isEnabled(event, "mediaononlychannel")) return;

        // Is message in mo-channel?
        var channel = event.getChannel();
        var guild = event.getGuild();
        var internalGuild = new moderation.guild.Guild(guild.getIdLong());
        try {
            var lang = internalGuild.getLanguage();
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
                    mb.setContent(String.format(LanguageHandler.get(lang, "mediaonlychannel_warning"), author.getAsMention()));
                    new MessageHandler().sendAndExpire(channel, mb.build(), 30 * 1000); // 30 seconds.
                }
            }
        } catch (SQLException e) {
            new Log(e, event.getGuild(), event.getAuthor(), "mediaonlychannel", null).sendLog(false);
        }
    }

    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        var guild = event.getGuild();
        var internalGuild = new Guild(guild.getIdLong());
        try {
            internalGuild.unsetMediaOnlyChannel(event.getChannel());
        } catch (SQLException e) {
            new Log(e, guild, null, "mediaonlychannel", null).sendLog(false);
        }
    }

    public void onGuildLeave(GuildLeaveEvent event) {
        var guild = event.getGuild();
        var internalGuild = new Guild(guild.getIdLong());
        try {
            internalGuild.unsetMediaOnlyChannels();
        } catch (SQLException e) {
            new Log(e, guild, null, "mediaonlychannel", null).sendLog(false);
        }
    }
}
