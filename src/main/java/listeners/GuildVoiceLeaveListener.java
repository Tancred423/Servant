package listeners;

import files.language.LanguageHandler;
import plugins.moderation.voicelobby.VoiceLobbyHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import servant.MyGuild;
import servant.MyUser;
import servant.Servant;
import utilities.Constants;
import utilities.ImageUtil;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GuildVoiceLeaveListener extends ListenerAdapter {
    // This event will be thrown if a user leaves a voice channel.
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        var member = event.getMember();
        var user = member.getUser();
        var myUser = new MyUser(user);
        var guild = event.getGuild();
        var myGuild = new MyGuild(guild);
        var guildOwner = guild.getOwner();
        if (guildOwner == null) return; // To eliminate errors. Will never occur.
        var myGuildOwnerUser = new MyUser(guildOwner.getUser());
        var jda = event.getJDA();
        var lang = myGuild.getLanguageCode();
        var channel = event.getChannelLeft();

        // Blacklist
        if (guild.getIdLong() == Constants.DISCORD_BOT_LIST_ID) return;
        if (guildOwner.getUser().isBot()) return;
        if (myGuild.isBlacklisted() || myUser.isBlacklisted()) return;

        CompletableFuture.runAsync(() -> {
            // Voice Lobby
            var activeIds = VoiceLobbyHandler.getActive(event.getJDA());
            /* No check for active plugins as the voice lobbies still should be removed properly
               even if someone turns the plugin off in the middle of usage. */
            if (activeIds.contains(channel.getIdLong())) {
                processVoiceLobby(event, guild.getIdLong(), activeIds, channel);
            }

            // Log
            if (myGuild.pluginIsEnabled("log") && myGuild.categoryIsEnabled("moderation") && myGuild.logIsEnabled("vc_leave")) {
                var logChannel = guild.getTextChannelById(myGuild.getLogChannelId());
                if (logChannel != null) {
                    logChannel.sendMessage(new EmbedBuilder()
                            .setColor(myGuildOwnerUser.getColor())
                            .setTitle(LanguageHandler.get(lang, "log_vc_leave_title"))
                            .addField(LanguageHandler.get(lang, "user"), event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator() + "\n" + event.getMember().getUser().getId(), true)
                            .addField(LanguageHandler.get(lang, "vc"), event.getChannelLeft().getName() + "\n" + event.getChannelLeft().getId(), false)
                            .setFooter(LanguageHandler.get(lang, "log_at"), ImageUtil.getUrl(jda, "clock"))
                            .setTimestamp(Instant.now())
                            .build()
                    ).queue();
                }
            }
        }, Servant.fixedThreadPool);
    }

    private static void processVoiceLobby(GuildVoiceLeaveEvent event, long guildId, List<Long> activeIds, VoiceChannel channel) {
        var active = new LinkedList<VoiceChannel>();
        for (var activeId : activeIds) {
            if (event.getJDA().getVoiceChannelById(activeId) != null) active.add(event.getJDA().getVoiceChannelById(activeId));
            else new VoiceLobbyHandler(event.getJDA(), guildId, activeId).unsetActive();
        }

        if (active.contains(channel) && channel.getMembers().size() == 0) {
            channel.delete().queue();
            active.remove(channel);
        }
    }
}
