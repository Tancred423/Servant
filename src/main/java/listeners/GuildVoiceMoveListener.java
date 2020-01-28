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
import java.util.concurrent.TimeUnit;

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
        if (Blacklist.isBlacklisted(user, guild)) return;

        CompletableFuture.runAsync(() -> {
            var channel = event.getChannelJoined();
            var server = new Server(guild);
            var channels = server.getLobbies();
            var lang = server.getLanguage();

            // Voice Lobby
            if (Toggle.isEnabled(event, "voicelobby")) {
                if (channels.contains(channel.getIdLong())) processVoiceLobby(event, guild, server, user, member, lang);
            }
        }, Servant.threadPool);
    }

    private static void processVoiceLobby(GuildVoiceMoveEvent event, net.dv8tion.jda.api.entities.Guild guild, Server internalGuild, User user, Member member, String lang) {
        var active = new LinkedList<VoiceChannel>();
        // Join
        var joinedChannel = event.getChannelJoined();
        joinedChannel.createCopy().queue(newChannel ->
                newChannel.getManager().setName(VoiceLobby.getLobbyName(member, lang)).queue(name ->
                        newChannel.getManager().setParent(joinedChannel.getParent()).queue(parent ->
                                guild.modifyVoiceChannelPositions().selectPosition(newChannel).moveTo(joinedChannel.getPosition() + 1).queue(position ->
                                        guild.moveVoiceMember(member, newChannel).queue(move -> {
                                            internalGuild.setActiveLobby(newChannel.getIdLong());

                                            try {
                                                TimeUnit.MILLISECONDS.sleep(2000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            var activeIds = internalGuild.getActiveLobbies();

                                            for (var activeId : activeIds) {
                                                if (event.getJDA().getVoiceChannelById(activeId) != null)
                                                    active.add(event.getJDA().getVoiceChannelById(activeId));
                                                else internalGuild.unsetActiveLobby(activeId);
                                            }

                                            for (int i = 0; i < active.size(); i++) {
                                                if (active.get(i).getMembers().size() == 0) {
                                                    var vc = event.getJDA().getVoiceChannelById(active.get(i).getIdLong());
                                                    if (vc != null) {
                                                        vc.delete().queue();
                                                        active.remove(active.get(i));
                                                    }
                                                }
                                            }
                                        })
                                )
                        )
                ), failure -> System.out.println("Couldn't create new voice channel. User: " + user.getName() + "#" + user.getDiscriminator() + " (" + user.getIdLong() + ") Guild: " + guild.getName() + " (" + guild.getIdLong() + ")")
        );

        // Leave
        var leftChannel = event.getChannelLeft();
        if (active.contains(leftChannel) && leftChannel.getMembers().size() == 0) {
            leftChannel.delete().queue();
            active.remove(leftChannel);
        }
    }
}
