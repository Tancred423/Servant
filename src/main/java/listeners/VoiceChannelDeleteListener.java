package listeners;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.ImageUtil;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class VoiceChannelDeleteListener extends ListenerAdapter {
    // This event will be thrown if a voice channel gets deleted.
    public void onVoiceChannelDelete(@Nonnull VoiceChannelDeleteEvent event) {
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);
        var guildOwner = guild.getOwner();
        if (guildOwner == null) return; // To eliminate errors. Will never occur.
        var guildOwnerUser = guildOwner.getUser();
        var myGuildOwnerUser = new MyUser(guildOwnerUser);
        var jda = event.getJDA();
        var lang = myGuild.getLanguageCode();
        var vc = event.getChannel();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (guildOwnerUser.isBot()) return;
        if (myGuild.isBlacklisted() || myGuildOwnerUser.isBlacklisted()) return;

        CompletableFuture.runAsync(() -> {
            // Log
            if (myGuild.pluginIsEnabled("log") && myGuild.categoryIsEnabled("moderation") && myGuild.logIsEnabled("tc_delete")) {
                var logChannel = guild.getTextChannelById(myGuild.getLogChannelId());
                if (logChannel != null) {
                    logChannel.sendMessage(new EmbedBuilder()
                            .setColor(myGuildOwnerUser.getColor())
                            .setTitle(LanguageHandler.get(lang, "log_vc_delete_title"))
                            .addField(LanguageHandler.get(lang, "log_vc_name"), event.getChannel().getName(), true)
                            .addField(LanguageHandler.get(lang, "vc_id"), event.getChannel().getId(), true)
                            .setFooter(LanguageHandler.get(lang, "log_at"), ImageUtil.getUrl(jda, "clock"))
                            .setTimestamp(Instant.now())
                            .build()
                    ).queue();
                }
            }

            // PURGES
            myGuild.purgeVc(vc.getIdLong());
        }, Servant.fixedThreadPool);
    }
}
