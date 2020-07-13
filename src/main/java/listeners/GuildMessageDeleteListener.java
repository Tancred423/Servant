package listeners;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.ImageUtil;

import java.awt.*;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

public class GuildMessageDeleteListener extends ListenerAdapter {
    // This event will be thrown if a messages gets deleted on a guild.
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);
        var guildOwner = guild.getOwner();
        if (guildOwner == null) return; // To eliminate errors. Will never occur.
        var jda = event.getJDA();
        var msgId = event.getMessageIdLong();
        var lang = myGuild.getLanguageCode();
        var myMessage = Servant.myMessageCache.get(msgId);
        var author = myMessage == null ? null : myMessage.getAuthor();
        var myAuthor = author == null ? null : new MyUser(author);
        var contentRaw = myMessage == null ? null : myMessage.getContent();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (author != null && author.isBot()) return;
        if (myGuild.isBlacklisted()) return;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                CompletableFuture.runAsync(() -> {
                    // Log
                    if (myGuild.pluginIsEnabled("log") && myGuild.categoryIsEnabled("moderation") && myGuild.logIsEnabled("msg_delete") && Servant.myDeletedMessageCache.get(msgId) == null) {
                        var logChannel = guild.getTextChannelById(myGuild.getLogChannelId());
                        if (logChannel != null) {
                            logChannel.sendMessage(new EmbedBuilder()
                                    .setColor(myAuthor == null ? Color.decode(Servant.config.getDefaultColorCode()) : myAuthor.getColor())
                                    .setTitle(LanguageHandler.get(lang, "log_msg_delete_title"))
                                    .addField(LanguageHandler.get(lang, "author"), author == null ? "_" + LanguageHandler.get(lang, "log_msg_too_old_author") + "..._" : author.getName() + "#" + author.getDiscriminator(), true)
                                    .addField(LanguageHandler.get(lang, "msg_id"), String.valueOf(msgId), true)
                                    .addField(LanguageHandler.get(lang, "log_msg_old_content"), contentRaw == null ? "_" + LanguageHandler.get(lang, "log_msg_too_old_content") + "..._" : contentRaw, false)
                                    .setFooter(LanguageHandler.get(lang, "log_at"), ImageUtil.getUrl(jda, "clock"))
                                    .setTimestamp(Instant.now())
                                    .build()
                            ).queue();
                        }
                    }

                    // PURGES
                    myGuild.purgeMsg(event.getChannel().getIdLong(), event.getMessageIdLong());
                }, Servant.fixedThreadPool);
            }
        }, 5000);
    }
}
