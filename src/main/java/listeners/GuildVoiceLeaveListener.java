package listeners;

import moderation.toggle.Toggle;
import moderation.voicelobby.VoiceLobby;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GuildVoiceLeaveListener extends ListenerAdapter {
    // This event will be thrown if a user leaves a voice channel.
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        var guild = event.getGuild();
        var member = event.getMember();
        var user = member.getUser();

        /* Certain conditions must meet, so this event is allowed to be executed:
         * 1.   Ignore any request from the Discord Bot List as this big guild
         *      invoke a lot of events, but never use this bot actively.
         * 2.   Ignore any request from bots to prevent infinite loops.
         * 3.   Ignore any request from blacklisted users and guilds.
         */
        if (guild.getIdLong() == 264445053596991498L) return; // Discord Bot List
        if (user.isBot()) return;
        if (Blacklist.isBlacklisted(guild, user)) return;

        CompletableFuture.runAsync(() -> {
            var channel = event.getChannelJoined();

            // Voice Lobby
            var activeIds = VoiceLobby.getActive(event.getJDA());
            if (Toggle.isEnabled(event, "voicelobby")) {
                processVoiceLobby(event, guild.getIdLong(), activeIds, channel);
            }
        }, Servant.fixedThreadPool);
    }

    private static void processVoiceLobby(GuildVoiceLeaveEvent event, long guildId, List<Long> activeIds, VoiceChannel channel) {
        var active = new LinkedList<VoiceChannel>();
        for (var activeId : activeIds) {
            if (event.getJDA().getVoiceChannelById(activeId) != null) active.add(event.getJDA().getVoiceChannelById(activeId));
            else new VoiceLobby(event.getJDA(), guildId, activeId).unsetActive();
        }

        if (active.contains(channel) && channel.getMembers().size() == 0) {
            channel.delete().queue();
            active.remove(channel);
        }
    }
}
