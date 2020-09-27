package listeners;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.ImageUtil;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class EmoteAddedListener extends ListenerAdapter {
    public void onEmoteAdded(@Nonnull EmoteAddedEvent event) {
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);
        var guildOwner = guild.getOwner();
        if (guildOwner == null) return; // To eliminate errors. Will never occur.
        var myGuildOwnerUser = new MyUser(guildOwner.getUser());
        var jda = event.getJDA();
        var lang = myGuild.getLanguageCode();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (guildOwner.getUser().isBot()) return;
        if (myGuild.isBlacklisted()) return;

        CompletableFuture.runAsync(() -> {
            // Log
            if (myGuild.pluginIsEnabled("log") && myGuild.categoryIsEnabled("moderation") && myGuild.logIsEnabled("emote_add")) {
                var logChannel = guild.getTextChannelById(myGuild.getLogChannelId());
                if (logChannel != null) {
                    logChannel.sendMessage(new EmbedBuilder()
                            .setColor(myGuildOwnerUser.getColor())
                            .setTitle(LanguageHandler.get(lang, "log_emote_add_title"))
                            .addField(LanguageHandler.get(lang, "log_emote"), event.getEmote().getName() + "\n" + event.getEmote().getId(), true)
                            .addField(LanguageHandler.get(lang, "emote"), event.getEmote().getAsMention(), false)
                            .setFooter(LanguageHandler.get(lang, "log_at"), ImageUtil.getUrl(jda, "clock"))
                            .setTimestamp(Instant.now())
                            .build()
                    ).queue();
                }
            }
        }, Servant.fixedThreadPool);
    }
}
