package listeners;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.ImageUtil;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class GuildMessageUpdateListener extends ListenerAdapter {
    // This event will be thrown if a messages gets deleted on a guild.
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);
        var guildOwner = guild.getOwner();
        if (guildOwner == null) return; // To eliminate errors. Will never occur.
        var jda = event.getJDA();
        var message = event.getMessage();
        var msgId = event.getMessageIdLong();
        var lang = myGuild.getLanguageCode();
        var author = event.getAuthor();
        var myAuthor = new MyUser(author);

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (author.isBot()) return;
        if (myGuild.isBlacklisted() || myAuthor.isBlacklisted()) return;

        CompletableFuture.runAsync(() -> {
            var myMessage = Servant.myMessageCache.get(msgId);
            var contentRaw = myMessage == null ? null : myMessage.getContent();

            // Log
            if (myGuild.pluginIsEnabled("log") && myGuild.categoryIsEnabled("moderation") && myGuild.logIsEnabled("msg_update")) {
                var logChannel = guild.getTextChannelById(myGuild.getLogChannelId());
                if (logChannel != null) {
                    logChannel.sendMessage(new EmbedBuilder()
                            .setColor(myAuthor.getColor())
                            .setTitle(LanguageHandler.get(lang, "log_msg_update_title"))
                            .addField(LanguageHandler.get(lang, "author"), author.getName() + "#" + author.getDiscriminator(), true)
                            .addField(LanguageHandler.get(lang, "msg_id"), String.valueOf(msgId), true)
                            .addField(LanguageHandler.get(lang, "log_msg_old_content"), contentRaw == null ? "_" + LanguageHandler.get(lang, "log_msg_too_old_content") + "..._" : contentRaw, false)
                            .addField(LanguageHandler.get(lang, "log_msg_new_content"), message.getContentRaw(), false)
                            .setFooter(LanguageHandler.get(lang, "log_at"), ImageUtil.getUrl(jda, "clock"))
                            .setTimestamp(Instant.now())
                            .build()
                    ).queue();
                }
            }
        }, Servant.fixedThreadPool);
    }
}
