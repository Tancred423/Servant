package listeners;

import moderation.guild.Server;
import moderation.toggle.Toggle;
import moderation.voicelobby.VoiceLobby;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import owner.blacklist.Blacklist;
import servant.Servant;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public class GuildVoiceJoinListener extends ListenerAdapter {
    // This event will be thrown if a user joins a voice channel.
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
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
            var server = new Server(guild);
            var channels = server.getVoiceLobbies();
            var lang = server.getLanguage();

            // Voice Lobby
            if (Toggle.isEnabled(event, "voicelobby")) {
                if (channels.contains(channel.getIdLong()))
                    processVoiceLobby(event, guild, user, member, channel, lang);
            }
        }, Servant.fixedThreadPool);
    }

    private static void processVoiceLobby(GuildVoiceJoinEvent event, net.dv8tion.jda.api.entities.Guild guild, User user, Member member, VoiceChannel channel, String lang) {
        var actives = new LinkedList<VoiceChannel>();
        channel.createCopy().queue(newChannel ->
                newChannel.getManager().setParent(channel.getParent()).queue(parent ->
                        newChannel.getManager().setName(VoiceLobby.getVoiceLobbyName(member, lang)).queue(name ->
                                guild.modifyVoiceChannelPositions().selectPosition(newChannel).moveTo(channel.getPosition() + 1).queue(position -> {
                                    try {
                                        guild.moveVoiceMember(member, newChannel).queue(move -> {
                                            new VoiceLobby(event.getJDA(), guild.getIdLong(), newChannel.getIdLong()).setActive();
                                            new java.util.Timer().schedule(
                                                    new java.util.TimerTask() {
                                                        @Override
                                                        public void run() {
                                                            var activeIds = VoiceLobby.getActive(event.getJDA());

                                                            for (var activeId : activeIds)
                                                                if (event.getJDA().getVoiceChannelById(activeId) != null)
                                                                    actives.add(event.getJDA().getVoiceChannelById(activeId));
                                                                else
                                                                    new VoiceLobby(event.getJDA(), guild.getIdLong(), activeId).unsetActive();

                                                            for (int i = 0; i < actives.size(); i++) {
                                                                if (actives.get(i).getMembers().size() == 0) {
                                                                    var vc = event.getJDA().getVoiceChannelById(actives.get(i).getIdLong());
                                                                    if (vc != null) {
                                                                        vc.delete().queue(success -> {
                                                                        }, failure -> {
                                                                        });
                                                                        actives.remove(actives.get(i));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }, 2000
                                            );
                                        });
                                    } catch (IllegalStateException e) {
                                        var activeIds = VoiceLobby.getActive(event.getJDA());

                                        for (var activeId : activeIds)
                                            if (event.getJDA().getVoiceChannelById(activeId) != null)
                                                actives.add(event.getJDA().getVoiceChannelById(activeId));
                                            else
                                                new VoiceLobby(event.getJDA(), guild.getIdLong(), activeId).unsetActive();

                                        for (var active : actives)
                                            if (active.getMembers().size() == 0) active.delete().queue();
                                    }
                                })
                        ), failure -> System.out.println("Couldn't create new voice channel. User: " + user.getName() + "#" + user.getDiscriminator() + " (" + user.getIdLong() + ") Guild: " + guild.getName() + " (" + guild.getIdLong() + ")")
                )
        );
    }
}
