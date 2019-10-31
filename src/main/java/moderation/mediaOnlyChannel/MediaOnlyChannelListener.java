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
import utilities.MessageHandler;
import utilities.Parser;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MediaOnlyChannelListener extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!Toggle.isEnabled(event, "mediaononlychannel")) return;

            var author = event.getAuthor();
            if (author.isBot()) return;

            // Is message in mo-channel?
            var channel = event.getChannel();
            var guild = event.getGuild();
            var internalGuild = new moderation.guild.Guild(guild.getIdLong());
            var lang = internalGuild.getLanguage(guild, author);
            if (internalGuild.mediaOnlyChannelHasEntry(channel, guild, author)) {
                // Has to have an attachment or a valid url.
                var validMessage = true;
                var message = event.getMessage();

                List<Message.Attachment> attachments = message.getAttachments();
                if (attachments.isEmpty()) {
                    String url = null;
                    var args = event.getMessage().getContentDisplay().split(" ");
                    for (var arg : args)
                        if (arg.startsWith("http")) url = arg;
                        else if (arg.startsWith("||http") && arg.endsWith("||")) url = arg.substring(2, arg.length() - 2); // killing spoiler tags

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
        });
    }

    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var internalGuild = new Guild(guild.getIdLong());
            internalGuild.unsetMediaOnlyChannel(event.getChannel(), guild, guild.getOwner().getUser());
        });
    }

    public void onGuildLeave(GuildLeaveEvent event) {
        CompletableFuture.runAsync(() -> {
            var guild = event.getGuild();
            var internalGuild = new Guild(guild.getIdLong());
            internalGuild.unsetMediaOnlyChannels(guild, guild.getOwner().getUser());
        });
    }
}
