package listeners;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.ImageUtil;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class TextChannelCreateListener extends ListenerAdapter {
    // This event will be thrown if a text channel gets deleted.
    public void onTextChannelCreate(TextChannelCreateEvent event) {
        var guild = event.getGuild();
        var guildOwner = guild.getOwner();
        if (guildOwner == null) return; // To eliminate errors. Will never occur.
        var guildOwnerUser = guildOwner.getUser();
        var myGuildOwnerUser = new MyUser(guildOwnerUser);
        var myGuild = new MyGuild(guild);
        var jda = event.getJDA();
        var lang = myGuild.getLanguageCode();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (guildOwnerUser.isBot()) return;
        if (myGuild.isBlacklisted() || myGuildOwnerUser.isBlacklisted()) return;

        CompletableFuture.runAsync(() -> {
            // Log
            if (myGuild.pluginIsEnabled("log") && myGuild.categoryIsEnabled("moderation") && myGuild.logIsEnabled("tc_create")) {
                var logChannel = guild.getTextChannelById(myGuild.getLogChannelId());
                if (logChannel != null) {
                    logChannel.sendMessage(new EmbedBuilder()
                            .setColor(myGuildOwnerUser.getColor())
                            .setTitle(LanguageHandler.get(lang, "log_tc_create_title"))
                            .addField(LanguageHandler.get(lang, "log_tc_name"), event.getChannel().getName() + "\n" + event.getChannel().getId(), true)
                            .setFooter(LanguageHandler.get(lang, "log_at"), ImageUtil.getUrl(jda, "clock"))
                            .setTimestamp(Instant.now())
                            .build()
                    ).queue();
                }
            }
        }, Servant.fixedThreadPool);
    }
}
