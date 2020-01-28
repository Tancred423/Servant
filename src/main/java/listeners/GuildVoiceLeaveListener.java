package listeners;

import moderation.guild.Server;
import moderation.toggle.Toggle;
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
        var owner = guild.getOwner();
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
        if (Blacklist.isBlacklisted(user, guild)) return;

        CompletableFuture.runAsync(() -> {
            var channel = event.getChannelJoined();
            var server = new Server(guild);
            var activeIds = server.getActiveLobbies();

            // Voice Lobby
            if (Toggle.isEnabled(event, "voicelobby")) {
                processVoiceLobby(event, guild, server, activeIds, channel);
            }
        }, Servant.threadPool);
    }

    private static void processVoiceLobby(GuildVoiceLeaveEvent event, net.dv8tion.jda.api.entities.Guild guild, Server internalGuild, List<Long> activeIds, VoiceChannel channel) {
        var active = new LinkedList<VoiceChannel>();
        for (var activeId : activeIds) {
            if (event.getJDA().getVoiceChannelById(activeId) != null) active.add(event.getJDA().getVoiceChannelById(activeId));
            else internalGuild.unsetActiveLobby(activeId);
        }

        if (active.contains(channel) && channel.getMembers().size() == 0) {
            channel.delete().queue();
            active.remove(channel);
        }
    }
}
