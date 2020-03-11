package listeners;

import moderation.guild.Server;
import moderation.toggle.Toggle;
import moderation.voicelobby.VoiceLobby;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public class GuildVoiceMoveListener extends ListenerAdapter {
    // This event will be thrown if a user moves to a voice channel from a vc.
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
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
            var server = new Server(guild);
            var lang = server.getLanguage();

            // Voice Lobby
            if (Toggle.isEnabled(event, "voicelobby")) {
                processVoiceLobby(event, guild, server, user, member, lang);
            }
        }, Servant.fixedThreadPool);
    }

    private static void processVoiceLobby(GuildVoiceMoveEvent event, net.dv8tion.jda.api.entities.Guild guild, Server server, User user, Member member, String lang) {
        var actives = new LinkedList<VoiceChannel>();
        var activeIds = VoiceLobby.getActive(event.getJDA());
        for (var activeId : activeIds)
            if (event.getJDA().getVoiceChannelById(activeId) != null)
                actives.add(event.getJDA().getVoiceChannelById(activeId));
            else
                new VoiceLobby(event.getJDA(), guild.getIdLong(), activeId).unsetActive();

        var channels = server.getVoiceLobbies();
        var joinedChannel = event.getChannelJoined();
        if (channels.contains(joinedChannel.getIdLong())) {
            // Join
            joinedChannel.createCopy().queue(newChannel -> {
                new VoiceLobby(event.getJDA(), guild.getIdLong(), newChannel.getIdLong()).setActive();
                newChannel.getManager().setParent(joinedChannel.getParent()).queue(
                        parent -> newChannel.getManager().setName(VoiceLobby.getVoiceLobbyName(member, lang)).queue(
                                name -> guild.modifyVoiceChannelPositions().selectPosition(newChannel).moveTo(joinedChannel.getPosition() + 1).queue(
                                        position -> {
                                            try {
                                                guild.moveVoiceMember(member, newChannel).queue();
                                            } catch (Exception ignored) { }
                                        }
                                )
                        ),
                        failure -> System.out.println("Couldn't create new voice channel. User: " + user.getName() + "#" + user.getDiscriminator() + " (" + user.getIdLong() + ") Guild: " + guild.getName() + " (" + guild.getIdLong() + ")")
                );

                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                if (newChannel.getMembers().size() == 0)
                                    newChannel.delete().queue(s -> {}, f -> {});
                            }
                        }, 3000
                );
            });
        }

        // Leave
        var leftChannel = event.getChannelLeft();
        if (actives.contains(leftChannel) && leftChannel.getMembers().size() == 0) {
            leftChannel.delete().queue();
            actives.remove(leftChannel);
        }
    }
}
