package listeners;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.ImageUtil;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class GuildUpdateBoostTierListener extends ListenerAdapter {
    // This event will be thrown if a text channel gets deleted.
    public void onGuildUpdateBoostTier(@Nonnull GuildUpdateBoostTierEvent event) {
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
            if (myGuild.pluginIsEnabled("log") && myGuild.categoryIsEnabled("moderation") && myGuild.logIsEnabled("boost_tier")) {
                var logChannel = guild.getTextChannelById(myGuild.getLogChannelId());
                if (logChannel != null) {
                    logChannel.sendMessage(new EmbedBuilder()
                            .setColor(myGuildOwnerUser.getColor())
                            .setTitle(LanguageHandler.get(lang, "log_boost_tier_title"))
                            .addField(LanguageHandler.get(lang, "log_new_boost_count"), String.valueOf(event.getNewBoostTier()), true)
                            .addField(LanguageHandler.get(lang, "log_old_boost_count"), String.valueOf(event.getOldBoostTier()), true)
                            .addField(LanguageHandler.get(lang, "log_changing_perks"),
                                    LanguageHandler.get(lang, "log_emote_slots") + ": " + event.getOldBoostTier().getMaxEmotes() + " → " + event.getNewBoostTier().getMaxEmotes() + "\n" +
                                    LanguageHandler.get(lang, "log_max_bitrate") + ": " + event.getOldBoostTier().getMaxBitrate() + " → " + event.getNewBoostTier().getMaxBitrate(),
                                    true)
                            .setFooter(LanguageHandler.get(lang, "log_at"), ImageUtil.getUrl(jda, "clock"))
                            .setTimestamp(Instant.now())
                            .build()
                    ).queue();
                }
            }
        }, Servant.fixedThreadPool);
    }
}
